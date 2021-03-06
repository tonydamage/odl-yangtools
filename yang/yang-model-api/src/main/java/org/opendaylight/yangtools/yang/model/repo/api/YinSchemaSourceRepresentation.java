/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.model.repo.api;

/**
 * A YIN {@link SchemaSourceRepresentation}.
 */
public interface YinSchemaSourceRepresentation extends SchemaSourceRepresentation {
    /**
     * Return the concrete representation type.
     *
     * @return The type of representation.
     */
    @Override
    Class<? extends YinSchemaSourceRepresentation> getType();
}
