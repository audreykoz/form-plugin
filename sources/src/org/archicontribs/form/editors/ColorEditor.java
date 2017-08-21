package org.archicontribs.form.editors;

import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class ColorEditor {
	private Label      lblColor;
	private Label      lblSample;
	private Button     btnSelectBackground;
	private Button     btnSelectForeground;
	private Button     btnResetToDefault;
	private Composite  parent;
	
	public ColorEditor(Composite parent) {
		this.parent = parent;
		
		lblColor = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblColor.setLayoutData(fd);
        lblColor.setText("Color:");
        
        btnResetToDefault = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        btnResetToDefault.setLayoutData(fd);
        btnResetToDefault.setImage(FormGraphicalEditor.binImage);
        btnResetToDefault.addSelectionListener(colorReset);
        btnResetToDefault.setToolTipText("Reset to the default color.");
        
        btnSelectBackground = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(btnResetToDefault, -5);
        btnSelectBackground.setLayoutData(fd);
        btnSelectBackground.setText("B");
        btnSelectBackground.addSelectionListener(colorChooser);
        btnSelectBackground.setToolTipText("Select the background color.");
        
        btnSelectForeground = new Button(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblColor, 0, SWT.CENTER);
        fd.right = new FormAttachment(btnSelectBackground, -5);
        btnSelectForeground.setLayoutData(fd);
        btnSelectForeground.setText("F");
        btnSelectForeground.addSelectionListener(colorChooser);
        btnSelectForeground.setToolTipText("Select the foreground color.");
        
        lblSample = new Label(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblColor, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(btnSelectForeground, -5);
        lblSample.setLayoutData(fd);
        lblSample.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
	}
	
    private SelectionAdapter colorChooser = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	Widget     widget = (Widget)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	Color color;
        	
    		ColorDialog dlg = new ColorDialog((Shell)parent.getData("shell"));
    		if ( event.getSource() == btnSelectForeground ) {
    			dlg.setRGB(lblSample.getForeground().getRGB());
    			dlg.setText("Choose the foreground color");
    		} else {
    			dlg.setRGB(lblSample.getBackground().getRGB());
    			dlg.setText("Choose the background color");
    		}
    		
    		RGB rgb = dlg.open();
    		if (rgb != null) {
   				color = (event.getSource() == btnSelectForeground) ? lblSample.getForeground() : lblSample.getBackground();
				if ( color != null )
					color.dispose();
				color = new Color(FormGraphicalEditor.display, rgb);
				
				if ( event.getSource() == btnSelectForeground ) {
					lblSample.setForeground(color);
					
					if ( widget != null ) {
						if ( widget instanceof Shell )
							((Shell)widget).setForeground(color);
						else
							((Control)widget).setForeground(color);
					}
					
	    			if ( treeItem != null )
	    				treeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
				} else {
					lblSample.setBackground(color);
					
					if ( widget != null ) {
						if ( widget instanceof Shell )
							((Shell)widget).setBackground(color);
						else
							((Control)widget).setBackground(color);
					}
					
					if ( treeItem != null )
	    				treeItem.setData("Background", rgb.red+","+rgb.green+","+rgb.blue);
				}
    		}
    	}
    };
    
    private SelectionAdapter colorReset = new SelectionAdapter() {
        @Override
    	public void widgetSelected(SelectionEvent event) {
        	Widget     widget = (Widget)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	Color color;
        	
			color = lblSample.getForeground();
			if ( color != null )
				color.dispose();
			
			color = lblSample.getBackground();
			if ( color != null )
				color.dispose();
			
			lblSample.setBackground(null);
			lblSample.setForeground(null);
			
			if ( widget != null ) {
				if ( widget instanceof Shell ) {
					((Shell)widget).setBackground(null);
					((Shell)widget).setForeground(null);
				} else {
					((Control)widget).setBackground(null);
					((Control)widget).setForeground(null);
				}
			}
			
			if ( treeItem != null ) {
				treeItem.setData("background", "");
				treeItem.setData("foreground", "");
			}
        }
    };
    
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblColor.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblColor.setLayoutData(fd);
	}
	
	public Label getControl() {
		return lblSample;
	}
    
    public void setBackround(String rgbString) {
    	Color color = null;

    	if ( !FormPlugin.isEmpty(rgbString) ) {
    		String rgb[] = rgbString.split(",");
    		
    		if ( rgb.length == 3 ) {
    			color = lblSample.getBackground();
    			if ( color != null )
    				color.dispose();
			
    			color = new Color(FormGraphicalEditor.display, Integer.valueOf(rgb[0].trim()),Integer.valueOf(rgb[1].trim()),Integer.valueOf(rgb[2].trim()));
    		}
    	}
		
    	lblSample.setBackground(color);
    }
    
    public void setForeground(String rgbString) {
    	Color color = null;
    	
    	if ( !FormPlugin.isEmpty(rgbString) ) {
	    	String rgb[] = rgbString.split(",");
	    	if ( rgb.length == 3 ) {
	    		color = lblSample.getForeground();
				if ( color != null )
					color.dispose();
				
				color = new Color(FormGraphicalEditor.display, Integer.valueOf(rgb[0].trim()),Integer.valueOf(rgb[1].trim()),Integer.valueOf(rgb[2].trim()));
	    	}
    	}
    	
		lblSample.setForeground(color);
    }
    
    public String getBackground() {
    	Color color = lblSample.getBackground();
    	return color.getRed()+","+color.getGreen()+","+color.getBlue();
    }
    
    public String getForeground() {
    	Color color = lblSample.getBackground();
    	return color.getRed()+","+color.getGreen()+","+color.getBlue();
    }
}