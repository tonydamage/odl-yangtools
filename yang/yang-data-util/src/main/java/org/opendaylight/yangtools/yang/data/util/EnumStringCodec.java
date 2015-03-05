/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import com.google.common.collect.Sets;
import java.util.Set;
import org.opendaylight.yangtools.yang.data.api.codec.EnumCodec;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.EnumTypeDefinition.EnumPair;

final class EnumStringCodec extends LeafSimpleValueStringCodec<String, EnumTypeDefinition> implements
        EnumCodec<String> {

    protected EnumStringCodec(final EnumTypeDefinition typeDef) {
        super(typeDef, String.class);
    }

    @Override
    public String deserialize(final String stringRepresentation) {
        Set<String> allowedNames = Sets.newHashSet();
        for (EnumPair pair : getTypeDefinition().getValues()) {
            allowedNames.add(pair.getName());
        }

        if (!allowedNames.contains(stringRepresentation)) {
            throw new IllegalArgumentException("Invalid value \"" + stringRepresentation
                    + "\" for enum type. Allowed values are: " + allowedNames);
        }
        return stringRepresentation;
    }

    @Override
    public String serialize(final String data) {
        return data == null ? "" : data;
    }
}