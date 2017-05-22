package org.archicontribs.form.menu;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Level;
import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormLogger;
import org.archicontribs.form.FormPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.archimatetool.canvas.editparts.CanvasBlockEditPart;
import com.archimatetool.canvas.editparts.CanvasDiagramPart;
import com.archimatetool.canvas.editparts.CanvasStickyEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateDiagramPart;
import com.archimatetool.editor.diagram.editparts.ArchimateElementEditPart;
import com.archimatetool.editor.diagram.editparts.ArchimateRelationshipEditPart;
import com.archimatetool.editor.diagram.editparts.DiagramConnectionEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.DiagramImageEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.GroupEditPart;
import com.archimatetool.editor.diagram.editparts.diagram.NoteEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchActorEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchDiagramPart;
import com.archimatetool.editor.diagram.sketch.editparts.SketchGroupEditPart;
import com.archimatetool.editor.diagram.sketch.editparts.StickyEditPart;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;



public class FormMenu extends ExtensionContributionFactory {
	private static final FormLogger logger = new FormLogger(FormMenu.class);

	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		Object[] selection = ((IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()).toArray();
		ImageDescriptor formMenuIcon = ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle(FormPlugin.PLUGIN_ID), new Path("icons/form.jpg"), null));
		boolean addSeparator = true;
		int menuEntriesLimit = 10;
		int menuEntries = 0;
		String configFilename;
		
		try {
			configFilename = Paths.get(FormPlugin.configFilePath).toRealPath().toString();
		} catch (IOException e1) {
			configFilename = FormPlugin.configFilePath;
		}
		
		File f = new File(configFilename);

		if( !f.exists() || f.isDirectory() ) {
			FormDialog.popup(Level.ERROR, "Configuration file not found:\n"+configFilename);
			return;
		}

		if ( !f.canRead() ) {
			FormDialog.popup(Level.ERROR, "Cannot read configuration file:\n"+configFilename+"\n\nPermission denied.");
			return;
		}

		try {
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(configFilename));
			int version = FormDialog.getInt(json, "version");
			if ( version != 2 )
				throw new RuntimeException("Not the right version (should be 2).");

			JSONArray forms = FormDialog.getJSONArray(json, FormPlugin.PLUGIN_ID);

			// we loop over the forms
			for ( int formRank = 0; formRank < forms.size(); ++formRank ) {
				JSONObject form = (JSONObject) forms.get(formRank);
				
				HashSet<EObject>selected = new HashSet<EObject>();
				boolean refersToView = false;
				boolean refersToModel = false;

				String name = FormDialog.getString(form,"name");
				if ( name.isEmpty() )
					throw new RuntimeException("Form names cannot be empty.");
				
				String refers = FormDialog.getString(form,"refers", "");
				switch ( refers.toLowerCase() ) {
					case "":
					case "selected" : break;
					case "view" : refersToView = true; break;
					case "model" : refersToModel = true; break;
					default : throw new RuntimeException("Unknown \"refers\" value \""+refers+"\".\n\nMust be \"selected\", \"view\" or \"model\".");
				}
				
				if ( logger.isDebugEnabled() ) logger.debug("Found form \""+name+"\"");

				JSONObject filter = FormDialog.getJSONObject(form, "filter", null);
				if ( (filter != null) && logger.isDebugEnabled() ) logger.debug("Applying filter to selected components");

				//we loop over the selected components
				for ( int selectionRank = 0; selectionRank < selection.length; ++selectionRank ) {
					Object obj = selection[selectionRank];
					EObject selectedObject;
					switch ( obj.getClass().getSimpleName() ) {
						case "ArchimateElementEditPart" : selectedObject = ((ArchimateElementEditPart)obj).getModel(); break;
						case "ArchimateRelationshipEditPart" : selectedObject = ((ArchimateRelationshipEditPart)obj).getModel(); break;
						case "ArchimateDiagramPart" : selectedObject = ((ArchimateDiagramPart)obj).getModel(); break;
						case "CanvasDiagramPart" : selectedObject = ((CanvasDiagramPart)obj).getModel(); break;
						case "SketchDiagramPart" : selectedObject = ((SketchDiagramPart)obj).getModel(); break;
						case "CanvasBlockEditPart" : selectedObject = ((CanvasBlockEditPart)obj).getModel(); break;
						case "CanvasStickyEditPart" : selectedObject = ((CanvasStickyEditPart)obj).getModel(); break;
						case "DiagramConnectionEditPart" : selectedObject = ((DiagramConnectionEditPart)obj).getModel(); break;
						case "DiagramImageEditPart" : selectedObject = ((DiagramImageEditPart)obj).getModel(); break;
						case "GroupEditPart" : selectedObject = ((GroupEditPart)obj).getModel(); break;
						case "NoteEditPart" : selectedObject = ((NoteEditPart)obj).getModel(); break;
						case "SketchActorEditPart" : selectedObject = ((SketchActorEditPart)obj).getModel(); break;
						case "SketchGroupEditPart" : selectedObject = ((SketchGroupEditPart)obj).getModel(); break;
						case "StickyEditPart" : selectedObject = ((StickyEditPart)obj).getModel(); break;
						default : throw new RuntimeException("Don't know how to deal with objects of class "+obj.getClass().getSimpleName());
					}
					
					if ( refersToView ) {
				        while ( !(selectedObject instanceof IDiagramModel) ) {
				        	selectedObject = selectedObject.eContainer();
				        }
					}
					else if ( refersToModel ) {
						if ( selectedObject instanceof IArchimateDiagramModel )
							selectedObject = ((IArchimateDiagramModel)selectedObject).getArchimateModel();
						else
							selectedObject = ((IDiagramModelArchimateObject)selectedObject).getDiagramModel().getArchimateModel();
					}
					
					// we guarantee than an object is not included in the same menu several times
					if ( !selected.contains(selectedObject) && ((filter == null) || FormDialog.checkFilter(selectedObject, filter)) ) {
						String menuLabel = FormDialog.expand(name, selectedObject);
						Map<String, Object> commandParameters = new HashMap<String, Object>();
						commandParameters.put("org.archicontribs.form.formRank", String.valueOf(formRank));
						commandParameters.put("org.archicontribs.form.selectionRank", String.valueOf(selectionRank));

						CommandContributionItemParameter p = new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(),	// serviceLocator
								"org.archicontribs.form.formMenuContributionItem",		// id
								"org.archicontribs.form.showForm",						// commandId
								commandParameters,										// parameters
								formMenuIcon,												// icon
								null,													// disabledIcon
								null,													// hoverIcon
								menuLabel,												// label
								null,													// mnemonic
								null,													// tooltip 
								CommandContributionItem.STYLE_PUSH,						// style
								null,													// helpContextId
								true);													// visibleEnabled

						if ( logger.isDebugEnabled() ) logger.debug("Adding menu \""+name+"\"");

						CommandContributionItem item = new CommandContributionItem(p);
						item.setVisible(true);
						if ( addSeparator ) {
							additions.addContributionItem(new Separator(), null);
							addSeparator = false;
						}
						additions.addContributionItem(item, null);
						if ( ++menuEntries >= menuEntriesLimit ) {
							if ( logger.isDebugEnabled() ) logger.debug("Limit of "+menuEntriesLimit+" reached. No more menu entries will be created");
							return;
						}
						selected.add(selectedObject);
					}
				}
			}
		} catch (IOException e) {
			FormDialog.popup(Level.ERROR, "I/O Error while reading configuration file:\n"+configFilename,e);
		} catch (ParseException e) {
			if ( e.getMessage() !=null )
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename,e);
			else
				FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename+"\n\nUnexpected "+e.getUnexpectedObject().toString()+" at position "+e.getPosition());
		}  catch (ClassCastException e) {
			FormDialog.popup(Level.ERROR, "Wrong key type in the configuration files:\n"+configFilename,e);
		} catch (RuntimeException e) {
			FormDialog.popup(Level.ERROR, "Parsing error while reading configuration file:\n"+configFilename,e);
		}
	}
}