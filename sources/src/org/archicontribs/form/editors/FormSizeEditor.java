package org.archicontribs.form.editors;

import org.archicontribs.form.FormDialog;
import org.archicontribs.form.FormGraphicalEditor;
import org.archicontribs.form.FormPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TreeItem;

public class FormSizeEditor {
	private Label      lblWidth;
	private StyledText txtWidth;
	private Label      lblHeight;
	private StyledText txtHeight;
	private Label      lblSpacing;
	private StyledText txtSpacing;
	private Composite  parent;
	
	public FormSizeEditor(Composite parent) {
		this.parent = parent;
		
		// width
		lblWidth = new Label(parent, SWT.NONE);
        FormData fd = new FormData();
        fd.top = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblWidth.setLayoutData(fd);
        lblWidth.setText("Width:");
        
        txtWidth = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        txtWidth.setLayoutData(fd);
        txtWidth.setTextLimit(4);
        txtWidth.addVerifyListener(numericVerifyListener);
        txtWidth.addModifyListener(sizeModifyListener);
        txtWidth.setToolTipText("Width of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogWidth+"."
        		);
        
        // height
        lblHeight = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblWidth, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblHeight.setLayoutData(fd);
        lblHeight.setText("Height:");
        
        txtHeight = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        txtHeight.setLayoutData(fd);
        txtHeight.setTextLimit(4);
        txtHeight.addVerifyListener(numericVerifyListener);
        txtHeight.addModifyListener(sizeModifyListener);
        txtHeight.setToolTipText("Height of the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogHeight+"."
        		);
        
        // spacing
        lblSpacing = new Label(parent, SWT.NONE);
        fd = new FormData();
        fd.top = new FormAttachment(lblHeight, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblSpacing.setLayoutData(fd);
        lblSpacing.setText("Spacing:");
        
        txtSpacing = new StyledText(parent, SWT.BORDER);
        fd = new FormData();
        fd.top = new FormAttachment(lblSpacing, 0, SWT.TOP);
        fd.left = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        fd.right = new FormAttachment(100, -FormGraphicalEditor.editorBorderMargin);
        txtSpacing.setLayoutData(fd);
        txtSpacing.setTextLimit(2);
        txtSpacing.addVerifyListener(numericVerifyListener);
        txtSpacing.addModifyListener(sizeModifyListener);
        txtSpacing.setToolTipText("Space to leave bewteen the form border and the tab in the form dialog.\n"+
        		"\n"+
        		"Default: "+FormDialog.defaultDialogSpacing+"."
        		);
	}
	
    VerifyListener numericVerifyListener = new VerifyListener() {
        @Override
    	public void verifyText(VerifyEvent e) {
          String string = e.text;
          char[] chars = new char[string.length()];
          string.getChars(0, chars.length, chars, 0);
          for (int i = 0; i < chars.length; i++) {
            if (!('0' <= chars[i] && chars[i] <= '9')) {
              e.doit = false;
              return;
            }
          }
        }
    };
	
	private ModifyListener sizeModifyListener = new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        	Shell      form = (Shell)parent.getData("control");
        	TreeItem   treeItem = (TreeItem)parent.getData("treeItem");
        	
        	int formWidth = getWidth();
        	int formHeight = getHeight();
        	int formSpacing = getSpacing();
        	
        	if ( treeItem != null ) {
        		treeItem.setData("width", formWidth);
        		treeItem.setData("height", formHeight);
        		treeItem.setData("spacing", formSpacing);
        	}
        	
        	if ( form != null ) {    	
		    	form.setSize(formWidth, formHeight);
				// we resize the form because we want the width and height to be the client's area width and height
		    	//TODO : size of the tab would be better ???
		        Rectangle area = form.getClientArea();
		        formWidth = formWidth * 2 - area.width;
		        formHeight = formHeight * 2 - area.height;
		        form.setSize(formWidth, formHeight);
		        
		        TabFolder tabFolder = (TabFolder)form.getData("tabFolder");
		        if ( tabFolder != null ) {
		            int buttonWidth = (int)treeItem.getData("buttonWidth");
		            int buttonHeight = (int)treeItem.getData("buttonHeight");
		            
		            area = form.getClientArea();
		            int tabFolderWidth = area.width - formSpacing * 2;
		            int tabFolderHeight = area.height - formSpacing * 3 - buttonHeight;
		            
		            tabFolder.setBounds(formSpacing, formSpacing, tabFolderWidth, tabFolderHeight);
		            
		            Button buttonCancel = (Button)form.getData("buttonCancel");
		            if ( buttonCancel != null ) {
		            	buttonCancel.setBounds(tabFolderWidth+formSpacing-buttonWidth, tabFolderHeight+formSpacing*2, buttonWidth, buttonHeight);
		            }
		            
		            Button buttonOk = (Button)form.getData("buttonOk");
		            if ( buttonOk != null ) {
		            	buttonOk.setBounds(tabFolderWidth-(buttonWidth*2), tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
		            }
		            
		            Button buttonExport = (Button)form.getData("buttonExport");
		            if ( buttonExport != null ) {
		            	buttonExport.setBounds(tabFolderWidth-(buttonWidth*3)-formSpacing, tabFolderHeight+(formSpacing*2), buttonWidth, buttonHeight);
			        }
		        }
        	}
    	}
    };
	
	public void setPosition(int position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblWidth.setLayoutData(fd);
	}
	
	public void setPosition(Control position) {
        FormData fd = new FormData();
        fd.top = new FormAttachment(position, FormGraphicalEditor.editorVerticalMargin);
        fd.left = new FormAttachment(0, FormGraphicalEditor.editorBorderMargin);
        fd.right = new FormAttachment(FormGraphicalEditor.editorLeftposition, 0);
        lblWidth.setLayoutData(fd);
	}
	
	public StyledText getControl() {
		return txtSpacing;
	}
    
    public void setWidth(int width) {
		txtWidth.removeModifyListener(sizeModifyListener);
		txtWidth.setText(String.valueOf(width));
		txtWidth.addModifyListener(sizeModifyListener);
    }
    
    public void setHeight(int height) {
		txtHeight.removeModifyListener(sizeModifyListener);
		txtHeight.setText(String.valueOf(height));
		txtHeight.addModifyListener(sizeModifyListener);
    }
    
    public void setSpacing(int spacing) {
		txtSpacing.removeModifyListener(sizeModifyListener);
		txtSpacing.setText(String.valueOf(spacing));
		txtSpacing.addModifyListener(sizeModifyListener);
    }
    
    public int getWidth() {
		if ( FormPlugin.isEmpty(txtWidth.getText()) )
			return FormDialog.defaultDialogWidth;
		if ( Integer.valueOf(txtWidth.getText()) < 50 )
			return 50;
		return Integer.valueOf(txtWidth.getText());
    }
    
    public int getHeight() {
		if ( FormPlugin.isEmpty(txtHeight.getText()) )
			return FormDialog.defaultDialogHeight;
		if ( Integer.valueOf(txtHeight.getText()) < 50 )
			return 50;
		return Integer.valueOf(txtHeight.getText());
    }
    
    public int getSpacing() {
		if ( FormPlugin.isEmpty(txtSpacing.getText()) )
			return FormDialog.defaultDialogSpacing;
		return Integer.valueOf(txtSpacing.getText());
    }
}