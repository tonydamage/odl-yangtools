/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import com.google.common.base.Preconditions;
import org.opendaylight.yangtools.yang.data.api.codec.UnionCodec;
import org.opendaylight.yangtools.yang.model.api.TypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.UnionTypeDefinition;

final class UnionStringCodec implements UnionCodec<String> {

    private UnionTypeDefinition typeDef;

    protected UnionStringCodec(final UnionTypeDefinition typeDef) {
        this.typeDef = typeDef;
    }

    @Override
    public String serialize(final Object data) {
        return data == null ? "" : data.toString();
    }

    @Override
    public Object deserialize(final String stringRepresentation) {
        boolean valid = false;
        for (TypeDefinition<?> type : typeDef.getTypes()) {
            LeafSimpleValueStringCodec<?, ?> typeAwareCodec = LeafSimpleValueStringCodec.from(type);
            if (typeAwareCodec == null) {
                // This is a type for which we have no codec (eg identity ref)
                // so we'll say it's valid
                // but we'll continue in case there's another type for which we
                // do have a codec.
                valid = true;
                continue;
            }

            try {
                typeAwareCodec.deserialize(stringRepresentation);
                valid = true;
                break;
            } catch (Exception e) {
                // invalid - try the next union type.
            }
        }
        Preconditions.checkArgument(valid, "Invalid value '%s' for %s type", typeDef.getQName());
        return stringRepresentation;
    }
}