/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yangtools.yang.stmt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.opendaylight.yangtools.yang.model.api.DataSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ChoiceCaseNode;
import org.opendaylight.yangtools.yang.model.api.ChoiceSchemaNode;
import java.io.FileNotFoundException;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import org.junit.Test;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.common.QNameModule;
import org.opendaylight.yangtools.yang.model.api.AnyXmlSchemaNode;
import org.opendaylight.yangtools.yang.model.api.ContainerSchemaNode;
import org.opendaylight.yangtools.yang.model.api.GroupingDefinition;
import org.opendaylight.yangtools.yang.model.api.ListSchemaNode;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.meta.ModelStatement;
import org.opendaylight.yangtools.yang.parser.spi.meta.ReactorException;
import org.opendaylight.yangtools.yang.parser.spi.meta.SomeModifiersUnresolvedException;
import org.opendaylight.yangtools.yang.parser.spi.source.SourceException;
import org.opendaylight.yangtools.yang.parser.spi.source.StatementStreamSource;
import org.opendaylight.yangtools.yang.parser.stmt.reactor.CrossSourceStatementReactor;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangInferencePipeline;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.YangStatementSourceImpl;
import org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.EffectiveSchemaContext;
import org.opendaylight.yangtools.yang.common.SimpleDateFormatUtil;

public class AugmentProcessTest {

    private static final YangStatementSourceImpl AUGMENTED = new YangStatementSourceImpl(
            "/stmt-test/augments/augmented.yang", false);
    private static final YangStatementSourceImpl ROOT = new YangStatementSourceImpl(
            "/stmt-test/augments/aug-root.yang", false);

    private static final QNameModule ROOT_QNAME_MODULE = QNameModule.create(
            URI.create("root"), SimpleDateFormatUtil.DEFAULT_DATE_REV);
    private static final QNameModule AUGMENTED_QNAME_MODULE = QNameModule
            .create(URI.create("aug"), SimpleDateFormatUtil.DEFAULT_DATE_REV);

    private static GroupingDefinition grp2Def;

    private final QName augParent1 = QName.create(AUGMENTED_QNAME_MODULE,
            "aug-parent1");
    private final QName augParent2 = QName.create(AUGMENTED_QNAME_MODULE,
            "aug-parent2");
    private final QName contTarget = QName.create(AUGMENTED_QNAME_MODULE,
            "cont-target");

    private final QName contAdded1 = QName.create(ROOT_QNAME_MODULE,
            "cont-added1");
    private final QName contAdded2 = QName.create(ROOT_QNAME_MODULE,
            "cont-added2");

    private final QName list1 = QName.create(ROOT_QNAME_MODULE, "list1");
    private final QName axml = QName.create(ROOT_QNAME_MODULE, "axml");

    private final QName contGrp = QName.create(ROOT_QNAME_MODULE,
            "cont-grp");
    private final QName axmlGrp = QName.create(ROOT_QNAME_MODULE,
            "axml-grp");

    private final QName augCont1 = QName.create(ROOT_QNAME_MODULE, "aug-cont1");
    private final QName augCont2 = QName.create(ROOT_QNAME_MODULE, "aug-cont2");

    private final QName grpCont2 = QName.create(ROOT_QNAME_MODULE, "grp-cont2");
    private final QName grpCont22 = QName.create(ROOT_QNAME_MODULE,
            "grp-cont22");
    private final QName grpAdd = QName.create(ROOT_QNAME_MODULE, "grp-add");

    private static final YangStatementSourceImpl MULTIPLE_AUGMENT = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-test.yang",false);

    private static final YangStatementSourceImpl MULTIPLE_AUGMENT_ROOT = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-root.yang",false);
    private static final YangStatementSourceImpl MULTIPLE_AUGMENT_IMPORTED = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-imported.yang",false);
    private static final YangStatementSourceImpl MULTIPLE_AUGMENT_SUBMODULE = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-submodule.yang",false);

    private static final YangStatementSourceImpl MULTIPLE_AUGMENT_INCORRECT = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-incorrect.yang",false);

    private static final YangStatementSourceImpl MULTIPLE_AUGMENT_INCORRECT2 = new YangStatementSourceImpl(
            "/stmt-test/augments/multiple-augment-incorrect2.yang",false);

    @Test
    public void multipleAugmentsAndMultipleModulesTest()
            throws SourceException, ReactorException {
        CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR
                .newBuild();
        addSources(reactor, MULTIPLE_AUGMENT_ROOT, MULTIPLE_AUGMENT_IMPORTED,
                MULTIPLE_AUGMENT_SUBMODULE);

        EffectiveSchemaContext result = null;
        result = reactor.buildEffective();
        assertNotNull(result);
    }

    @Test
    public void multipleAugmentTest() throws SourceException, ReactorException {
        CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR
                .newBuild();
        addSources(reactor, MULTIPLE_AUGMENT);

        EffectiveSchemaContext result = null;
        result = reactor.buildEffective();
        assertNotNull(result);
    }

    @Test(expected=SomeModifiersUnresolvedException.class)
    public void multipleAugmentIncorrectPathTest() throws SourceException,
            ReactorException {
        CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR
                .newBuild();
        addSources(reactor, MULTIPLE_AUGMENT_INCORRECT);

        EffectiveSchemaContext result = null;
        result = reactor.buildEffective();
        assertNull(result);
    }

    @Test(expected=SomeModifiersUnresolvedException.class)
    public void multipleAugmentIncorrectPathAndGrpTest()
            throws SourceException, ReactorException {
        CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR
                .newBuild();
        addSources(reactor, MULTIPLE_AUGMENT_INCORRECT2);
        EffectiveSchemaContext result = null;
        result = reactor.buildEffective();
        assertNull(result);
    }



    @Test
    public void readAndParseYangFileTest() throws SourceException,
            ReactorException {

        CrossSourceStatementReactor.BuildAction reactor = YangInferencePipeline.RFC6020_REACTOR
                .newBuild();
        addSources(reactor, AUGMENTED, ROOT);

        final EffectiveSchemaContext root = reactor.buildEffective();
        assertNotNull(root);

        Module augmentedModule = root.findModuleByName("augmented", null);
        assertNotNull(augmentedModule);

        ContainerSchemaNode augParent1Node = (ContainerSchemaNode) root
                .getDataChildByName(augParent1);
        ContainerSchemaNode augParent2Node = (ContainerSchemaNode) augParent1Node
                .getDataChildByName(augParent2);
        ContainerSchemaNode targetContNode = (ContainerSchemaNode) augParent2Node
                .getDataChildByName(contTarget);
        assertNotNull(targetContNode);

        assertNotNull(targetContNode.getChildNodes());
        assertEquals(3, targetContNode.getChildNodes().size());

        ContainerSchemaNode contAdded1Node = (ContainerSchemaNode) targetContNode
                .getDataChildByName(contAdded1);
        assertNotNull(contAdded1Node);
        ListSchemaNode list1Node = (ListSchemaNode) contAdded1Node
                .getDataChildByName(list1);
        assertNotNull(list1Node);

        ContainerSchemaNode contAdded2Node = (ContainerSchemaNode) targetContNode
                .getDataChildByName(contAdded2);
        assertNotNull(contAdded2Node);
        AnyXmlSchemaNode axmlNode = (AnyXmlSchemaNode) contAdded2Node
                .getDataChildByName(axml);
        assertNotNull(axmlNode);

        ContainerSchemaNode contGrpNode = (ContainerSchemaNode) targetContNode
                .getDataChildByName(contGrp);
        assertNotNull(contGrpNode);
        AnyXmlSchemaNode axmlGrpNode = (AnyXmlSchemaNode) contGrpNode
                .getDataChildByName(axmlGrp);
        assertNotNull(axmlGrpNode);

        ContainerSchemaNode augCont1Node = (ContainerSchemaNode) root
                .getDataChildByName(augCont1);
        ContainerSchemaNode augCont2Node = (ContainerSchemaNode) augCont1Node
                .getDataChildByName(augCont2);
        assertNotNull(augCont2Node);

        ContainerSchemaNode grpCont2Node = (ContainerSchemaNode) augCont2Node
                .getDataChildByName(grpCont2);
        ContainerSchemaNode grpCont22Node = (ContainerSchemaNode) grpCont2Node
                .getDataChildByName(grpCont22);
        assertNotNull(grpCont22Node);

        ContainerSchemaNode grpAddNode = (ContainerSchemaNode) grpCont22Node
                .getDataChildByName(grpAdd);
        assertNotNull(grpAddNode);
    }

    private static <T extends ModelStatement<?>> T findInStatements(final QName target,
            final ImmutableList<T> statements) {

        for (final T statement : statements) {
            if (target
                    .equals(statement.statementDefinition().getArgumentName())) {
                return statement;
            }
        }

        return null;
    }

    private static void addSources(final CrossSourceStatementReactor.BuildAction reactor,
            final StatementStreamSource... sources) {
        for (StatementStreamSource source : sources) {
            reactor.addSource(source);
        }
    }

    @Test
    public void caseShortHandAugmentingTest() throws SourceException, FileNotFoundException, ReactorException, URISyntaxException {

        SchemaContext context = StmtTestUtils.parseYangSources("/choice-case-type-test-models");

        assertNotNull(context);

        ContainerSchemaNode netconf =  (ContainerSchemaNode) context.getDataChildByName("netconf-state");
        ContainerSchemaNode datastores =  (ContainerSchemaNode) netconf.getDataChildByName("datastores");
        ListSchemaNode datastore =  (ListSchemaNode) datastores.getDataChildByName("datastore");
        ContainerSchemaNode locks =  (ContainerSchemaNode) datastore.getDataChildByName("locks");
        ChoiceSchemaNode lockType =  (ChoiceSchemaNode)locks.getDataChildByName("lock-type");

        ChoiceCaseNode leafAugCase = lockType.getCaseNodeByName("leaf-aug-case");
        assertTrue(leafAugCase.isAugmenting());
        DataSchemaNode leafAug = leafAugCase.getDataChildByName("leaf-aug-case");
        assertFalse(leafAug.isAugmenting());
    }

}
