package org.archicontribs.form.preferences;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.log4j.Level;
import org.archicontribs.form.FormLogger;
import org.archicontribs.form.FormPlugin;
import org.archicontribs.form.FormDialog;
import org.archicontribs.form.preferences.FormFileFieldEditor;
import org.archicontribs.form.preferences.FormTextFieldEditor;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbench;

/**
 * This class sets the preference page that will show up in Archi preference menu.
 * 
 * @author Herve Jouin
 */
public class FormPreferencePage extends FieldEditorPreferencePage	implements IWorkbenchPreferencePage {
	private static String[][] LOGGER_MODES = {{"Disabled", "disabled"}, {"Simple mode", "simple"}, {"Expert mode", "expert"}};
	private static String[][] LOGGER_LEVELS = {{"Fatal", "fatal"}, {"Error", "error"}, {"Warn", "warn"}, {"Info", "info"}, {"Debug", "debug"}, {"Trace", "trace"}};
	
	public static final Color COMPO_BACKGROUND_COLOR = new Color(null, 250, 250, 250);	// light grey
	public static final Color GROUP_BACKGROUND_COLOR = new Color(null, 235, 235, 235);	// light grey (a bit darker than compo background)
	public static final Color RED_COLOR = new Color(null, 240, 0, 0);				// red
	
	private static String HELP_ID = "com.archimatetool.help.FormPreferencePage";
	
	private Composite loggerComposite;
	
	private FormConfigFileTableEditor table;
	
	private RadioGroupFieldEditor loggerModeRadioGroupEditor;
	private FormFileFieldEditor filenameFileFieldEditor;
	private RadioGroupFieldEditor loggerLevelRadioGroupEditor;
	private FormTextFieldEditor expertTextFieldEditor;
	private Group simpleModeGroup;
	private Group expertModeGroup;
	
	private Button btnCheckForUpdateAtStartupButton;
	private boolean mouseOverHelpButton = false;
	public static final Image HELP_ICON = new Image(Display.getDefault(), FormDialog.class.getResourceAsStream("/img/28x28/help.png"));
	
	private FormLogger logger = new FormLogger(FormPreferencePage.class);
	
	private TabFolder tabFolder;
	
	public FormPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		if ( logger.isDebugEnabled() ) logger.debug("Setting preference store");
		setPreferenceStore(FormPlugin.INSTANCE.getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	protected void createFieldEditors() {
		if ( logger.isDebugEnabled() ) logger.debug("Creating field editors on preference page");
		
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getFieldEditorParent().getParent(), HELP_ID);
        
		tabFolder = new TabFolder(getFieldEditorParent(), SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.setBackground(GROUP_BACKGROUND_COLOR);
		
		// ********************************* */
		// * Behaviour tab  **************** */
		// ********************************* */
		
		Composite behaviourComposite = new Composite(tabFolder, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 8;
        behaviourComposite.setLayout(layout);
        
        RowLayout rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        rowLayout.pack = true;
        rowLayout.marginTop = 5;
        rowLayout.marginBottom = 5;
        rowLayout.justify = false;
        rowLayout.fill = false;
        behaviourComposite.setLayoutData(rowLayout);
        behaviourComposite.setBackground(GROUP_BACKGROUND_COLOR);
        
		TabItem behaviourTabItem = new TabItem(tabFolder, SWT.NONE);
        behaviourTabItem.setText("Behaviour");
        behaviourTabItem.setControl(behaviourComposite);
        		
        Group grpVersion = new Group(behaviourComposite, SWT.NONE);
		grpVersion.setBackground(COMPO_BACKGROUND_COLOR);
		grpVersion.setLayout(new FormLayout());
		grpVersion.setText("Version : ");
		
		Label versionLbl = new Label(grpVersion, SWT.NONE);
		versionLbl.setText("Actual version :");
		versionLbl.setBackground(COMPO_BACKGROUND_COLOR);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 10);
		versionLbl.setLayoutData(fd);
		
		Label versionValue = new Label(grpVersion, SWT.NONE);
		versionValue.setText(FormPlugin.pluginVersion);
		versionValue.setBackground(COMPO_BACKGROUND_COLOR);
		versionValue.setFont(FormDialog.BOLD_FONT);
		fd = new FormData();
		fd.top = new FormAttachment(versionLbl, 0, SWT.TOP);
		fd.left = new FormAttachment(versionLbl, 5);
		versionValue.setLayoutData(fd);
		
		Button checkUpdateButton = new Button(grpVersion, SWT.NONE);
		checkUpdateButton.setBackground(COMPO_BACKGROUND_COLOR);
		checkUpdateButton.setText("Check for update");
		fd = new FormData();
		fd.top = new FormAttachment(versionValue, 0, SWT.CENTER);
		fd.left = new FormAttachment(versionValue, 100);
		checkUpdateButton.setLayoutData(fd);
		checkUpdateButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) { FormPlugin.checkForUpdate(true); }
			public void widgetDefaultSelected(SelectionEvent e) { widgetSelected(e); }
		});
		
		btnCheckForUpdateAtStartupButton = new Button(grpVersion, SWT.CHECK);
		btnCheckForUpdateAtStartupButton.setBackground(COMPO_BACKGROUND_COLOR);
		btnCheckForUpdateAtStartupButton.setText("Automatically check for update at startup");
		fd = new FormData();
		fd.top = new FormAttachment(versionLbl, 5);
		fd.left = new FormAttachment(0, 10);
		btnCheckForUpdateAtStartupButton.setLayoutData(fd);
		btnCheckForUpdateAtStartupButton.setSelection(FormPlugin.INSTANCE.getPreferenceStore().getBoolean("checkForUpdateAtStartup"));
		
		GridData gd = new GridData();
		//gd.heightHint = 45;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		grpVersion.setLayoutData(gd);
		
		table = new FormConfigFileTableEditor("ConfigFiles", "", behaviourComposite);
		addField(table);
		
		Group grpHelp = new Group(behaviourComposite, SWT.NONE);
		grpHelp.setBackground(COMPO_BACKGROUND_COLOR);
        grpHelp.setLayout(new FormLayout());
        grpHelp.setText("Online help : ");
        
        gd = new GridData();
        //gd.heightHint = 40;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        grpHelp.setLayoutData(gd);
        
        Label btnHelp = new Label(grpHelp, SWT.NONE);
        btnHelp.addListener(SWT.MouseEnter, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = true; btnHelp.redraw(); } });
        btnHelp.addListener(SWT.MouseExit, new Listener() { @Override public void handleEvent(Event event) { mouseOverHelpButton = false; btnHelp.redraw(); } });
        btnHelp.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e)
            {
                 if ( mouseOverHelpButton ) e.gc.drawRoundRectangle(0, 0, 29, 29, 10, 10);
                 e.gc.drawImage(HELP_ICON, 2, 2);
            }
        });
        btnHelp.addListener(SWT.MouseUp, new Listener() { @Override public void handleEvent(Event event) { if ( logger.isDebugEnabled() ) logger.debug("Showing help : /"+FormPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/"+FormPlugin.PLUGIN_ID+"/help/html/configurePlugin.html"); } });
        fd = new FormData(30,30);
        fd.top = new FormAttachment(0, 11);
        fd.left = new FormAttachment(0, 10);
        btnHelp.setLayoutData(fd);
        
        Label helpLbl = new Label(grpHelp, SWT.NONE);
        helpLbl.setText("Click here to show up online help.");
        helpLbl.setBackground(COMPO_BACKGROUND_COLOR);
        fd = new FormData();
        fd.top = new FormAttachment(btnHelp, 0, SWT.CENTER);
        fd.left = new FormAttachment(btnHelp, 10);
        helpLbl.setLayoutData(fd);
		
		// ********************************* */
		// * Logging tab  ****************** */
		// ********************************* */
        loggerComposite = new Composite(tabFolder, SWT.NULL);
        rowLayout = new RowLayout();
        rowLayout.type = SWT.VERTICAL;
        loggerComposite.setLayout(rowLayout);
        
        TabItem loggerTabItem = new TabItem(tabFolder, SWT.NONE);
        loggerTabItem.setText("Logger");
        loggerTabItem.setControl(loggerComposite);
        
        Label note = new Label(loggerComposite, SWT.NONE);
        note = new Label(loggerComposite, SWT.NONE);
        note.setText(" Please be aware that enabling debug or, even more, trace level has got important impact on performances!\n Activate only if required.");
        note.setForeground(RED_COLOR);
    	
    	loggerModeRadioGroupEditor = new RadioGroupFieldEditor("loggerMode", "", 1, LOGGER_MODES, loggerComposite, true);
    	addField(loggerModeRadioGroupEditor);
    	
    	simpleModeGroup = new Group(loggerComposite, SWT.NONE);
    	simpleModeGroup.setLayout(new GridLayout(3, false));
    	gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 500;
        simpleModeGroup.setLayoutData(gd);
        
        
        loggerLevelRadioGroupEditor = new RadioGroupFieldEditor("loggerLevel", "", 6, LOGGER_LEVELS, simpleModeGroup, false);
        addField(loggerLevelRadioGroupEditor);
        
        filenameFileFieldEditor = new FormFileFieldEditor("loggerFilename", "Log filename : ", false, FileFieldEditor.VALIDATE_ON_KEY_STROKE, simpleModeGroup);
        addField(filenameFileFieldEditor);    
        
    	expertModeGroup = new Group(loggerComposite, SWT.NONE);
    	expertModeGroup.setLayout(new GridLayout());
    	gd = new GridData(GridData.FILL_BOTH);
    	gd.widthHint = 650;
    	expertModeGroup.setLayoutData(gd);
        
        expertTextFieldEditor = new FormTextFieldEditor("loggerExpert", "", expertModeGroup);
        expertTextFieldEditor.getTextControl().setLayoutData(gd);
        expertTextFieldEditor.getTextControl().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        addField(expertTextFieldEditor);

        showLogger();
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		
		if ( event.getSource() == loggerModeRadioGroupEditor )
			showLogger();
		
		 if( event.getSource() == filenameFileFieldEditor )
			 setValid(true);
	}
	
	private void showLogger() {
		String mode = null;
		
		// If the user changes the value, we get it
		for ( Control control: loggerModeRadioGroupEditor.getRadioBoxControl(loggerComposite).getChildren() ) {
			if (((Button)control).getSelection())
				mode = (String)((Button)control).getData();
		}
		
		// when the preference page initialize, the radioButton selection is not (yet) made.
		// so we get the value from the preferenceStore
		if ( mode == null ) {
			mode = FormPlugin.INSTANCE.getPreferenceStore().getString("loggerMode");
    		if ( mode == null ) {
    			mode = FormPlugin.INSTANCE.getPreferenceStore().getDefaultString("loggerMode");
    		}
		}
		
		// Defining of the user's choice, we show up the simple or expert parameters or none of them
		switch ( mode ) {
		case "disabled" :
			expertModeGroup.setVisible(false);
			simpleModeGroup.setVisible(false);
			break;
		case "simple" :
			expertModeGroup.setVisible(false);
			simpleModeGroup.setVisible(true);
			break;
		case "expert" :
			expertModeGroup.setVisible(true);
			simpleModeGroup.setVisible(false);
			break;
		default : 
			expertModeGroup.setVisible(false);
			simpleModeGroup.setVisible(false);
			logger.error("Unknown value \""+mode+"\" in loggerModeRadioGroupEditor.");
		}
	}
	
    @Override
    public boolean performOk() {
    	table.close();
    	
    	if ( logger.isTraceEnabled() ) logger.trace("Saving preferences in preference store");
    	
    	FormPlugin.INSTANCE.getPreferenceStore().setValue("checkForUpdateAtStartup", btnCheckForUpdateAtStartupButton.getSelection());
    	
    	table.store();
    	
   	    	// the loggerMode is a private property, so we use reflection to access it
		try {
			Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
			field.setAccessible(true);
			if ( logger.isTraceEnabled() ) logger.trace("loggerMode = "+(String)field.get(loggerModeRadioGroupEditor));
			field.setAccessible(false);
		} catch (Exception err) {
			logger.error("Failed to retrieve the \"loggerMode\" value from the preference page", err);
		}
		loggerModeRadioGroupEditor.store();
    	
    		// the loggerLevel is a private property, so we use reflection to access it
		try {
			Field field = RadioGroupFieldEditor.class.getDeclaredField("value");
			field.setAccessible(true);
			if ( logger.isTraceEnabled() ) logger.trace("loggerLevel = "+(String)field.get(loggerLevelRadioGroupEditor));
			field.setAccessible(false);
		} catch (Exception err) {
			logger.error("Failed to retrieve the \"loggerLevel\" value from the preference page", err);
		}
		loggerLevelRadioGroupEditor.store();
		
			//TODO : if we are in simple mode, check that is is a valid writable filename
		if ( logger.isTraceEnabled() ) logger.trace("loggerFilename = "+filenameFileFieldEditor.getStringValue());
		filenameFileFieldEditor.store();
		
		if ( logger.isTraceEnabled() ) logger.trace("loggerExpert = "+expertTextFieldEditor.getStringValue());
		expertTextFieldEditor.store();
		
        try {
        	if ( logger.isDebugEnabled() ) logger.debug("Saving the preference store to disk.");
            ((IPersistentPreferenceStore)FormPlugin.INSTANCE.getPreferenceStore()).save();
        } catch (IOException err) {
        	FormDialog.popup(Level.ERROR, "Failed to save the preference store to disk.", err);
        }
		
		try {
			logger.configure();
		} catch (Exception e) {
			FormDialog.popup(Level.ERROR, "Faied to configure logger", e);
		}
		
    	return true;
    }

	@Override
	public void init(IWorkbench workbench) {
	}

}