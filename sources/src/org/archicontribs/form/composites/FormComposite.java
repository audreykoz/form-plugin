package org.archicontribs.form.composites;

import java.util.List;
import java.util.Map;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.FilterEditor;
import org.archicontribs.form.editors.FormSizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class FormComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;         		// name
	private FormSizeEditor          formSizeEditor;    			// width, height, spacing, buttonWidth, buttonHeight						//TODO : rename spacing to margin
	private ColorEditor             colorEditor;        		// foreground, background
    private ComboEditor             refersEditor; 				// refers
    private FilterEditor			filterEditor;               // filter
    private StringEditor            variableSeparatorEditor;	// variableSeparator
    private StringEditor            buttonOkEditor; 			// buttonOk
    private StringEditor            buttonCancelEditor; 		// buttonCancel
    private StringEditor            buttonExportEditor; 		// buttonExport
    private ComboEditor             whenEmptyEditor;			// whenEmpty

	public FormComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		nameEditor = new StringEditor(this, "name", "Name:");
		nameEditor.setPosition(0);
		nameEditor.mustSetControlText(true);
		nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
				
        // width, height, spacing, buttonWidth, buttonHeight
		formSizeEditor = new FormSizeEditor(this);
		formSizeEditor.setPosition(nameEditor.getControl());
        
        // Background
		colorEditor = new ColorEditor(this, "Color:");
		colorEditor.setPosition(formSizeEditor.getControl());
		
		// Refers
		refersEditor = new ComboEditor(this, "refers", "Refers to:");
		refersEditor.setPosition(colorEditor.getControl());
		refersEditor.setItems(FormDialog.validRefers);
		refersEditor.setTooltipText("Choose which component the form will apply to:\n"+
                "   - Selected: the component(s) that will be selected when the form will be ran,\n"+
                "   - Folder: the folder in which the selected components are,\n"+
                "   - View: the view in which the selected components are,\n"+
                "   - Model: the whole model.\n"+
                "\n"+
                "Default: "+FormDialog.validRefers[0]+"."
                );
		
	    // filter
		filterEditor = new FilterEditor(this, false);
		filterEditor.setPosition(refersEditor.getControl());
		
	    // variableSeparator
		variableSeparatorEditor = new StringEditor(this, "variableSeparator", "Variable separator:");
		variableSeparatorEditor.setPosition(filterEditor.getControl());
		variableSeparatorEditor.setTextLimit(1);
		variableSeparatorEditor.setWidth(25);
		variableSeparatorEditor.setTooltipText("Character used to separate the different fields of a variable\n\nDefault: "+FormDialog.defaultVariableSeparator+".");
		
	    // buttonOk
		buttonOkEditor = new StringEditor(this, "buttonOk", "OK button text:");
		buttonOkEditor.setPosition(variableSeparatorEditor.getControl());
		buttonOkEditor.setWidget((Widget)getShell().getData("ok button"));
		buttonOkEditor.mustSetControlText(true);
		buttonOkEditor.setTooltipText("Text of the OK button\n\nDefault: "+FormDialog.defaultButtonOkText+".");
        
        // buttonCancel
		buttonCancelEditor = new StringEditor(this, "buttonCancel", "Cancel button text:");
		buttonCancelEditor.setPosition(buttonOkEditor.getControl());
		buttonCancelEditor.setWidget((Widget)getShell().getData("cancel button"));
		buttonCancelEditor.mustSetControlText(true);
		buttonCancelEditor.setTooltipText("Text of the Cancel button\n\nDefault: "+FormDialog.defaultButtonCancelText+".");
        
        // buttonExport
		buttonExportEditor = new StringEditor(this, "buttonExport", "Export button text:");
		buttonExportEditor.setPosition(buttonCancelEditor.getControl());
		buttonExportEditor.setWidget((Widget)getShell().getData("export button"));
		buttonExportEditor.mustSetControlText(true);
		buttonExportEditor.setTooltipText("Text of the Export to Excel button\n\nDefault: "+FormDialog.defaultButtonExportText+".");
		
        // whenEmpty
        whenEmptyEditor = new ComboEditor(this, "whenEmpty", "When empty :");
        whenEmptyEditor.setPosition(buttonExportEditor.getControl());
        whenEmptyEditor.setItems(FormDialog.validWhenEmpty);
        whenEmptyEditor.setTooltipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
                "   - ignore: do not change the property value:\n"+
                "                 - if the property does not already exist, it will not be created,\n"+
                "                 - if the propety does already exist, its value is left unmodified.\n"+
                "   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
                "   - delete: delete the property if it does already exist.\n"+
                "\n"+
                "Default: "+FormDialog.validWhenEmpty[0]+"."
                );
	}
    
    @SuppressWarnings("unchecked")
	public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
    		case "name":		      nameEditor.setText((String)value); break;
    		case "foreground":	      colorEditor.setForeground((String)value); break;
    		case "background":	      colorEditor.setBackground((String)value); break;
    		case "refers":		      refersEditor.setText((String)value); break;
    		case "variableseparator": variableSeparatorEditor.setText((String)value); break;
    		case "buttonok":	      buttonOkEditor.setText((String)value); break;
    		case "buttoncancel":      buttonCancelEditor.setText((String)value); break;
    		case "buttonexport":      buttonExportEditor.setText((String)value); break;
    		case "whenempty":         whenEmptyEditor.setText((String)value); break;
    		case "width":		      formSizeEditor.setWidth((Integer)value); break;
    		case "height":		      formSizeEditor.setHeight((Integer)value); break;
    		case "spacing":		      formSizeEditor.setSpacing((Integer)value); break;
    		case "buttonwidth":	      formSizeEditor.setButtonWidth((Integer)value); break;
    		case "buttonheight":      formSizeEditor.setButtonHeight((Integer)value); break;
            case "tests":             filterEditor.setTests((List<Map<String, String>>)value); break;
            case "genre":             filterEditor.setGenre((String)value); break;
    		default:
    			throw new RuntimeException("does not know key "+key);
    	}
    }
}
