/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.api.codec;

/**
 * Value codec for types, which are sensitive to introduction of additional models
 * into parsed context and values may carry XML namespace related information.
 *
 * Usually represented types which set of valid values may be affected by introduction
 * of additional model and may carry additional information.
 *
 * @param <P> Result of serialization
 * @param <I> Deserialized representation
 */
public interface ContextSensitiveCodec<P,I> extends ValueCodec<P, I, ContextSensitiveCodec<P, I>> {

}
