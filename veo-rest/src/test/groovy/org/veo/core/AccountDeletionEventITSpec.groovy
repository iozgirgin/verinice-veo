/*
 * verinice.veo
 * Copyright (C) 2026  Jonas Jordan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.veo.core

import static org.veo.core.events.MessageCreatorImpl.EVENT_TYPE_ACCOUNT_DELETION
import static org.veo.core.events.MessageCreatorImpl.EVENT_TYPE_CLIENT_CHANGE
import static org.veo.rest.VeoRestConfiguration.PROFILE_BACKGROUND_TASKS

import java.time.Instant

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.GenericContainer

import org.veo.core.entity.Client
import org.veo.core.entity.Unit
import org.veo.message.EventDispatcher
import org.veo.message.EventMessage
import org.veo.message.RabbitMQSenderConfiguration
import org.veo.message.TestContainersUtil
import org.veo.message.TestEventSubscriber

import groovy.util.logging.Slf4j
import spock.lang.AutoCleanup
import spock.lang.Shared

@SpringBootTest(classes = [TestEventSubscriber.class, RabbitMQSenderConfiguration.class])
@ActiveProfiles(["test", PROFILE_BACKGROUND_TASKS])
@Slf4j
class AccountDeletionEventITSpec extends VeoSpringSpec {
    @Shared
    @AutoCleanup("stop")
    private GenericContainer rabbit

    @Autowired
    EventDispatcher eventDispatcher

    @Value('${veo.message.routing-key-prefix}')
    String routingKeyPrefix

    @Value('${veo.message.exchanges.veo-subscriptions}')
    String exchange

    String messageType = EVENT_TYPE_ACCOUNT_DELETION
    String routingKey

    Client client
    Unit unit

    def setupSpec() {
        rabbit = TestContainersUtil.startRabbitMqContainer()
    }

    def setup() {
        routingKey = routingKeyPrefix + messageType
        client = createTestClient()
        unit = unitDataRepository.save(newUnit(client))
    }

    def "deleted username is removed from persons"() {
        given:
        def affectedPerson1Id = personDataRepository.save(newPerson(unit) {
            username = "herbat"
        }).id
        def affectedPerson2Id = personDataRepository.save(newPerson(unit) {
            username = "herbat"
        }).id
        def unaffectedPersonId = personDataRepository.save(newPerson(unit) {
            username = "robat"
        }).id

        when:
        eventDispatcher.send(exchange, new EventMessage(routingKey, """{
            "eventType": "$messageType",
            "clientId": "${client.id}",
            "username": "herbat"
        }""", 1, Instant.now()))

        then:
        defaultPolling.eventually {
            with(personDataRepository.findById(affectedPerson1Id).get()) {
                username == null
                version == 1
                changeNumber == 1
                updatedBy == "system"
            }
            with(personDataRepository.findById(affectedPerson2Id).get()) {
                username == null
                version == 1
                changeNumber == 1
                updatedBy == "system"
            }
        }

        and:
        with(personDataRepository.findById(unaffectedPersonId).get()) {
            username == "robat"
            version == 0
            changeNumber == 0
            updatedBy == "VeoRestMvcSpec entity factory"
        }
    }
}
