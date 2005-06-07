/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2003 Jeff Yoshimi <www.jeffyoshimi.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.simbrain.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.simbrain.network.pnodes.PNodeNeuron;
import org.simbrain.util.SFileChooser;
import org.simbrain.util.Utils;
import org.simnet.interfaces.Neuron;
import org.simnet.interfaces.Synapse;
import org.simnet.neurons.StandardNeuron;
import org.simnet.synapses.StandardSynapse;

import com.Ostermiller.util.CSVParser;

import edu.umd.cs.piccolo.PNode;

/**
 * <b>NetworkSerializer </b> contains the code for reading and writing network
 * files
 */
public class NetworkSerializer {

	private boolean isUsingTabs = true;

	public static final String FS = "/"; // System.getProperty("file.separator");Separator();

	private NetworkPanel parent_panel;
	
	private String currentDirectory = "./simulations/networks";

	private File current_file = null;


	/**
	 * Construct the serializer object
	 * 
	 * @param parent
	 *            reference to the panel containing the network to be saved
	 */
	public NetworkSerializer(NetworkPanel parent) {
		parent_panel = parent;
	}

	/**
	 * Show the dialog for choosing a network to open
	 */
	public void showOpenFileDialog() {

		SFileChooser chooser = new SFileChooser(currentDirectory, "xml");
		File theFile = chooser.showOpenDialog();
		if (theFile == null) {
			return;
		}
		readNetwork(theFile);
		currentDirectory = chooser.getCurrentLocation();
	}
	
	public void readNetwork(File f) {
		current_file = f;
		try {
			Reader reader = new FileReader(f);
			Mapping map = new Mapping();
			map.loadMapping("." + FS + "lib" + FS + "network_mapping.xml");
			Unmarshaller unmarshaller = new Unmarshaller(parent_panel);
			unmarshaller.setMapping(map);
			//unmarshaller.setDebug(true);
			parent_panel.getParentFrame().getWorkspace().getCouplingList().removeCouplings(parent_panel);
			parent_panel.resetNetwork();
			parent_panel = (NetworkPanel) unmarshaller.unmarshal(reader);
			parent_panel.initCastor();
			parent_panel.renderObjects();
			parent_panel.repaint();
			parent_panel.getParentFrame().getWorkspace().resetCommandTargets();
			
			//Set Path; used in workspace persistence
			String localDir = new String(System.getProperty("user.dir"));
			((NetworkFrame)parent_panel.getParentFrame()).setPath(Utils.getRelativePath(localDir, parent_panel.getCurrentFile().getAbsolutePath()));

			
		} catch (java.io.FileNotFoundException e) {
		    JOptionPane.showMessageDialog(null, "Could not read network file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();       
		    return;
		} catch (NullPointerException e){
		    JOptionPane.showMessageDialog(null, "Could not find network file \n"
			        + f, "Warning", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		catch (Exception e){
		    e.printStackTrace();
		    return;
		}
		parent_panel.getParentFrame().setTitle(f.getName());

	}
	

	/**
	 * Get a reference to the current network file
	 * 
	 * @return a reference to the current network file
	 */
	public File getCurrentFile() {
		return current_file;
	}


	/**
	 * Show the dialog for saving a network
	 */
	public void showSaveFileDialog() {

		SFileChooser chooser = new SFileChooser(currentDirectory, "xml");

		File theFile = chooser.showSaveDialog();
		if (theFile == null) {
			return;
		}

		writeNet(theFile);
		currentDirectory = chooser.getCurrentLocation();
	}

	/**
	 * Saves network information to the specified file
	 */
	public void writeNet(File theFile) {

		current_file = theFile;

		try {
			if (isUsingTabs == true) {
				LocalConfiguration.getInstance().getProperties().setProperty(
						"org.exolab.castor.indent", "true");
			} else {
				LocalConfiguration.getInstance().getProperties().setProperty(
						"org.exolab.castor.indent", "false");
			}
			FileWriter writer = new FileWriter(theFile);
			Mapping map = new Mapping();
			map.loadMapping("." + FS + "lib" + FS + "network_mapping.xml");
			Marshaller marshaller = new Marshaller(writer);
			marshaller.setMapping(map);
			//marshaller.setDebug(true);
			parent_panel.getNetwork().updateIds();
			parent_panel.updateIds();
			marshaller.marshal(parent_panel);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		parent_panel.getParentFrame().setTitle(theFile.getName());
		//System.gc();

	}

	/**
	 * @return Returns the isUsingTabs.
	 */
	public boolean isUsingTabs() {
		return isUsingTabs;
	}
	/**
	 * @param isUsingTabs The isUsingTabs to set.
	 */
	public void setUsingTabs(boolean isUsingTabs) {
		this.isUsingTabs = isUsingTabs;
	}
}