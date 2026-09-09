/*
 * verinice.veo
 * Copyright (C) 2019  Urs Zeidler.
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
package org.veo.persistence.access.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

import org.veo.persistence.entity.jpa.PersonData;

public interface PersonDataRepository extends CompositeEntityDataRepository<PersonData> {
  @Query("select p from #{#entityName} p " + "where p.username = ?1 and p.owner.client.id = ?2")
  List<PersonData> findAllByUsername(String username, UUID clientId);
}
