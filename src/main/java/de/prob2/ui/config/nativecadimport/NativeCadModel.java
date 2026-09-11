package de.prob2.ui.config.nativecadimport;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.Language;

public class NativeCadModel extends AbstractModel {

    public NativeCadModel(StateSpaceProvider stateSpaceProvider,
            Map<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
            DependencyGraph graph, File modelFile) {
        super(stateSpaceProvider, children, graph, modelFile);
        //TODO Auto-generated constructor stub
    }

    @Override
    public AbstractElement getComponent(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponent'");
    }

    @Override
    public AbstractElement getMainComponent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMainComponent'");
    }

    @Override
    public IEvalElement parseFormula(String formula, FormulaExpand expand) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseFormula'");
    }

    @Override
    public IEvalElement formulaFromIdentifier(List<String> identifier, FormulaExpand expansion) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'formulaFromIdentifier'");
    }

    @Override
    public FormalismType getFormalismType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFormalismType'");
    }

    @Override
    public Language getLanguage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLanguage'");
    }

    @Override
    public AbstractCommand getLoadCommand() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLoadCommand'");
    }
    
}
