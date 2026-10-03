/*
 * Copyright (C) 2026 DataGood contributors
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package org.veo.core.usecase

import org.veo.core.UserAccessRights
import org.veo.core.entity.Account
import org.veo.core.entity.AccountProvider
import org.veo.core.entity.Client
import org.veo.core.entity.Unit
import org.veo.core.entity.exception.NotFoundException
import org.veo.core.repository.DomainRepository
import org.veo.core.repository.ElementQuery
import org.veo.core.repository.GenericElementRepository
import org.veo.core.repository.PagedResult
import org.veo.core.repository.UnitRepository
import org.veo.core.usecase.unit.GetUnitDumpUseCase

import spock.lang.Specification
import spock.lang.Unroll

class DataGoodUnitExportAccessSpec extends Specification {
    def unitId = UUID.fromString('10000000-0000-4000-8000-000000000001')
    AccountProvider accounts = Mock()
    Account account = Mock()
    Client currentClient = Mock()
    Client foreignClient = Mock()
    Unit unit = Mock()
    UnitRepository units = Mock()
    GenericElementRepository elements = Mock()
    DomainRepository domains = Mock()
    UserAccessRights rights = Mock()
    ElementQuery query = Mock()
    PagedResult result = Mock()
    def useCase = new GetUnitDumpUseCase(accounts, elements, units, domains)

    def setup() {
        accounts.currentUserAccount >> account
        account.client >> currentClient
        elements.query(_) >> query
        query.execute(_) >> result
        result.resultPage() >> []
    }

    @Unroll
    def 'EXPORT-AUTH-#caseId foreign client is denied even when admin=#admin'() {
        given:
        account.isAdmin() >> admin
        unit.client >> foreignClient
        units.getById(unitId) >> unit // reproduces the old unsafe admin path
        units.getById(unitId, rights) >> { throw new NotFoundException(unitId, Unit.class) }

        when:
        useCase.execute(new GetUnitDumpUseCase.InputData(unitId, null), rights)

        then:
        thrown(NotFoundException)
        0 * elements.query(_)

        where:
        caseId | admin
        '01'   | true
        '02'   | false
    }

    @Unroll
    def 'EXPORT-AUTH-#caseId authorized export remains available when admin=#admin'() {
        given:
        account.isAdmin() >> admin
        unit.client >> currentClient
        units.getById(unitId) >> unit

        when:
        def output = useCase.execute(new GetUnitDumpUseCase.InputData(unitId, null), rights)

        then:
        1 * units.getById(unitId, rights) >> unit
        output.unit() == unit
        output.elements().empty

        where:
        caseId | admin
        '03'   | true
        '04'   | false
    }
}
