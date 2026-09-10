/*
 * verinice.veo
 * Copyright (C) 2022  Jonas Jordan
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
package org.veo.core.entity.inspection;

import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.veo.core.entity.DomainBase;
import org.veo.core.entity.ElementType;
import org.veo.core.entity.exception.NotFoundException;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/** Suggests a user action that would fix a {@link Finding} yielded by an {@link Inspection}. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = AddPartSuggestion.class, name = AddPartSuggestion.NAME),
})
@Schema(
    description = "Suggests an action to the user that would fix an inspection finding",
    discriminatorProperty = "type",
    discriminatorMapping =
        @DiscriminatorMapping(value = AddPartSuggestion.NAME, schema = AddPartSuggestion.class),
    oneOf = {AddPartSuggestion.class})
public interface Suggestion {

  /**
   * @param elementType Element type targeted by the inspection that holds this suggestion, or
   *     {@code null} if the inspection applies to all element types
   * @throws IllegalArgumentException If this suggestion is not applicable to an inspection that
   *     targets given element type, or if it references undefined domain contents
   * @throws NotFoundException If domain contents referenced by this suggestion cannot be found
   */
  void selfValidate(@Nullable ElementType elementType, DomainBase domain);

  @JsonIgnore
  default Set<String> getReferencedSubTypes() {
    return Set.of();
  }
}
