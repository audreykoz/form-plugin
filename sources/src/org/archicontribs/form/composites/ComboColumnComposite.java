package org.archicontribs.form.composites;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.editors.CheckEditor;
import org.archicontribs.form.editors.ColorEditor;
import org.archicontribs.form.editors.ComboEditor;
import org.archicontribs.form.editors.SizeEditor;
import org.archicontribs.form.editors.StringEditor;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class ComboColumnComposite extends Composite implements CompositeInterface {
	private StringEditor            nameEditor;          // name
	private StringEditor            commentEditor;       // comment
    private StringEditor            defaultTextEditor;   // defaultText
    private CheckEditor             forceDefaultEditor;  // forceDefault
    private StringEditor			valuesEditor;        // values		// TODO: needs to be rewritten using a distinct list editor
    private CheckEditor             editableEditor;  	 // editable
	private SizeEditor              sizeEditor;          // width
	private ColorEditor             colorEditor;         // foreground, background
	private StringEditor		    tooltipEditor;       // tooltip
	private ComboEditor             whenEmptyEditor;  	 // whenEmpty
	private StringEditor            excelColumnEditor;   // excelColumn
	private ComboEditor             excelCellTypeEditor; // excelCellType
	private ComboEditor             excelDefaultEditor;  // excelDefault


	public ComboColumnComposite(Composite parent, int style) {
		super(parent, style);
        setLayout(new FormLayout());
        createContent();
	}
	
	private void createContent() {
		// name
		this.nameEditor = new StringEditor(this, "name", "Name:");
		this.nameEditor.setPosition(0);
		this.nameEditor.mustSetTreeItemText(true);
		this.nameEditor.mustSetControlText(true);
		this.nameEditor.setTooltipText("Name of the object.\n\nThis can be any arbitrary text.");
		
		// comment
		this.commentEditor = new StringEditor(this, "comment", "Comment:");
		this.commentEditor.setPosition(this.nameEditor.getControl());
		this.commentEditor.setTooltipText("You may enter any comment you wish.\nJust press 'return' to enter several lines of text.");
		
	    // defaultText
        this.defaultTextEditor = new StringEditor(this, "default", "Default text:");
        this.defaultTextEditor.setPosition(this.commentEditor.getControl());
        this.defaultTextEditor.setTooltipText("Default value when the one corresponding to the variable value is empty.");
        
        // defaultText
        this.forceDefaultEditor = new CheckEditor(this, "forceDefault", "Force default:");
        this.forceDefaultEditor.setPosition(this.defaultTextEditor.getControl());
        this.forceDefaultEditor.setTooltipText("Force the default value even if the the variable value is not empty.");
        
        // values
        this.valuesEditor = new StringEditor(this, "values", "Values:");
        this.valuesEditor.setPosition(this.forceDefaultEditor.getControl());
        this.valuesEditor.setTooltipText("List of the valid values, one per line.");
        
        // editable
        this.editableEditor = new CheckEditor(this, "editable", "Read only:");
        this.editableEditor.setPosition(this.valuesEditor.getControl());
        this.editableEditor.setInverse(true);
        this.editableEditor.setTooltipText("Specifies if the variable is read only.\n\nDefault: false.");
        
        // Background
        this.colorEditor = new ColorEditor(this, "Color:");
        this.colorEditor.setPosition(this.editableEditor.getControl());
        
		// x, y, width, height
		this.sizeEditor = new SizeEditor(this);
		this.sizeEditor.setPosition(this.colorEditor.getControl());
		
		// tooltip
		this.tooltipEditor = new StringEditor(this, "tooltip", "Tooltip:");
		this.tooltipEditor.setPosition(this.sizeEditor.getControl());
		this.tooltipEditor.mustSetControlTolltip(true);
		this.tooltipEditor.setTooltipText("Specifies the tooltip to show when the mouse stands is over the control.\n\nDefault: none.");
		
		// whenempty
		this.whenEmptyEditor = new ComboEditor(this, "whenEmpty", "When empty:");
		this.whenEmptyEditor.setPosition(this.tooltipEditor.getControl());
		this.whenEmptyEditor.setItems(new String[] {"", "ignore", "create", "delete"});
        this.whenEmptyEditor.setTooltipText("Choose the plugin behaviour when a variable is left empty in the form:\n"+
                "   - ignore: do not change the property value:\n"+
                "                 - if the property does not already exist, it will not be created,\n"+
                "                 - if the propety does already exist, its value is left unmodified.\n"+
                "   - create: empty the property's value if it does already exist, or create a new one with an empty value,\n"+
                "   - delete: delete the property if it does already exist.\n"+
                "\n"+
                "Default: "+FormDialog.validWhenEmpty[0]+"."
                );
        
        // excelColumn
        this.excelColumnEditor = new StringEditor(this, "excelColumn", "Excel cell:");
        this.excelColumnEditor.setPosition(this.whenEmptyEditor.getControl());
        this.excelColumnEditor.setTooltipText("Adress of the Excel cell where the variable should be exported to (like A3 or D14).\n"+
        		"\n"+
        		"If the \"Excel sheet\" field is not set, then the variable will not be exported to Excel even if this field is set."
        		);
        
        // excelCellType
        this.excelCellTypeEditor = new ComboEditor(this, "excelType", "Excel type:");
        this.excelCellTypeEditor.setPosition(this.excelColumnEditor.getControl());
        this.excelCellTypeEditor.setItems(new String[] {"", "string", "boolean", "numeric", "formula"});
        this.excelCellTypeEditor.setTooltipText("Type of the Excel cell.\n\nDefault: string");
        
        // excelDefault
        this.excelDefaultEditor = new ComboEditor(this, "excelDefault", "Excel default:");
        this.excelDefaultEditor.setPosition(this.excelCellTypeEditor.getControl());
        this.excelDefaultEditor.setItems(new String[] {"", "blank", "zero", "delete"});
        this.excelDefaultEditor.setTooltipText("Behaviour of the plugin when exporting an empty value:\n"+
                "   - blank : a blank cell will be created (ie a cell with no content)\n"+
                "   - zero : a cell with a zero value in it:\n"+
                "                - 0 for numeric cells\n"+
                "                - empty string for string and formula cells\n"+
                "                - false for boolean cells\n"+
                "   - delete : the cell will be deleted.\n"+
                "\n"+
                "Default: blank");
	}
	
    @Override
    public void set(String key, Object value) throws RuntimeException {
    	switch ( key.toLowerCase() ) {
            case "name":          this.nameEditor.setText((String)value); break;
            case "comment":       this.commentEditor.setText((String)value); break;
            case "default":       this.defaultTextEditor.setText((String)value); break;
            case "forcedefault":  this.forceDefaultEditor.setChecked((Boolean)value); break;
            case "editable":      this.editableEditor.setChecked((Boolean)value); break;
            case "values":        this.valuesEditor.setText((String[])value); break;
    		case "x":			  this.sizeEditor.setX((Integer)value); break;
    		case "y":			  this.sizeEditor.setY((Integer)value); break;
    		case "width":		  this.sizeEditor.setWidth((Integer)value); break;
    		case "height":		  this.sizeEditor.setHeight((Integer)value); break;
    		case "foreground":    this.colorEditor.setForeground((String)value); break;
            case "background":    this.colorEditor.setBackground((String)value); break;
    		case "tooltip":    	  this.tooltipEditor.setText((String)value); break;
    		case "whenempty":     this.whenEmptyEditor.setText((String)value); break;
    		case "excelcolumn":	  this.excelColumnEditor.setText((String)value); break;
    		case "excelcelltype": this.excelCellTypeEditor.setText((String)value); break;
    		case "exceldefault":  this.excelDefaultEditor.setText((String)value); break;
    		default:			  throw new RuntimeException("does not know key "+key);
    	}
    }
}
