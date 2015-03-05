/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;
import org.opendaylight.yangtools.yang.data.api.codec.BitsCodec;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition;

final class BitsStringCodec extends LeafSimpleValueStringCodec<Set<String>, BitsTypeDefinition> implements
        BitsCodec<String> {

    public static final Joiner JOINER = Joiner.on(" ").skipNulls();
    public static final Splitter SPLITTER = Splitter.on(' ').omitEmptyStrings().trimResults();

    @SuppressWarnings("unchecked")
    protected BitsStringCodec(final BitsTypeDefinition typeDef) {
        super(typeDef, (Class<Set<String>>) ((Class<?>) Set.class));
    }

    @Override
    public String serialize(final Set<String> data) {
        return data == null ? "" : JOINER.join(data);
    }

    @Override
    public Set<String> deserialize(final String stringRepresentation) {
        if (stringRepresentation == null) {
            return ImmutableSet.of();
        }

        Iterable<String> strings = SPLITTER.split(stringRepresentation);

        Set<String> allowedNames = Sets.newHashSet();
        for (BitsTypeDefinition.Bit bit : getTypeDefinition().getBits()) {
            allowedNames.add(bit.getName());
        }

        for (String bit : strings) {
            if (!allowedNames.contains(bit)) {
                throw new IllegalArgumentException("Invalid value \"" + bit + "\" for bits type. Allowed values are: "
                        + allowedNames);
            }
        }

        return ImmutableSet.copyOf(strings);
    }
}