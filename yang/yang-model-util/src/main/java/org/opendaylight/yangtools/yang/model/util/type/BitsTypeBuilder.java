/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.model.util.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import javax.annotation.Nonnull;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition;
import org.opendaylight.yangtools.yang.model.api.type.BitsTypeDefinition.Bit;

public final class BitsTypeBuilder extends TypeBuilder<BitsTypeDefinition> {
    private final Builder<String, Bit> builder = ImmutableMap.builder();

    BitsTypeBuilder(final SchemaPath path) {
        super(null, path);
    }

    public void addBit(@Nonnull final Bit item) {
        builder.put(item.getName(), item);
    }

    @Override
    public BitsTypeDefinition build() {
        final Map<String, Bit> map = builder.build();

        // FIXME: run null position checks and re-generate Bit items as appropriate

        return new BaseBitsType(getPath(), getUnknownSchemaNodes(), map.values());
    }
}