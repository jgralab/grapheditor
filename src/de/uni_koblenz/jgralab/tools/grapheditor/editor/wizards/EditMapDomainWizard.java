package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.Wizard;
import org.pcollections.ArrayPMap;
import org.pcollections.PMap;
import org.pcollections.PSet;

import de.uni_koblenz.jgralab.schema.MapDomain;

public class EditMapDomainWizard extends Wizard {

	private EditMapDomainWizardPage page;
	
	private MapDomain domain;
	private PMap<?,?> value;
	
	public EditMapDomainWizard(MapDomain dom, PMap<?,?> val){
		super();
		this.domain = dom;
		this.value = val;
	}
	
	@Override
	public void addPages(){
		this.page = new EditMapDomainWizardPage(domain, value);
		addPage(page);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean performFinish() {
		TableViewer v = page.getViewer();

		PSet<Entry<?,?>> input = (PSet<Entry<?,?>>)v.getInput();
		ArrayPMap map = ArrayPMap.empty();

		for(Entry<?,?> e : input){
			map = map.plus(e.getKey(), e.getValue());
		}

		this.value = map;
		return true;
	}

	/**
	 * @return the value
	 */
	public PMap<?, ?> getValue() {
		return value;
	}

}
