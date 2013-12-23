/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
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
package org.simbrain.network.gui.dialogs.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.simbrain.network.groups.Group;
import org.simbrain.network.gui.NetworkPanel;
import org.simbrain.network.gui.dialogs.group.GroupPropertiesPanel;
import org.simbrain.network.subnetworks.Competitive;
import org.simbrain.network.subnetworks.Competitive.UpdateMethod;
import org.simbrain.util.LabelledItemPanel;

/**
 * <b>CompetitivePropertiesDialog</b> is a panel box for setting the properties
 * of a competitive network. Can either be used to create a new competitive
 * network or to edit an existing competitive network.
 */
public class CompetitivePropertiesPanel extends JPanel implements
        ActionListener, GroupPropertiesPanel {

    /** Default number of neurons. */
    private static final int DEFAULT_NUM_NEURONS = 5;

    /** Parent Network Panel. */
    private NetworkPanel networkPanel;

    /** Main Panel. */
    private LabelledItemPanel mainPanel = new LabelledItemPanel();

    /** Update method. */
    private JComboBox updateMethod = new JComboBox(
            Competitive.UpdateMethod.values());

    /** Number of neurons field. */
    private JTextField tfNumNeurons = new JTextField();

    /** Epsilon value field. */
    private JTextField tfEpsilon = new JTextField();

    /** Winner value field. */
    private JTextField tfWinnerValue = new JTextField();

    /** Loser value field. */
    private JTextField tfLoserValue = new JTextField();

    /** Leaky epsilon value. */
    private JTextField tfLeakyEpsilon = new JTextField();

    /** Leaky learning check box. */
    private JCheckBox cbUseLeakyLearning = new JCheckBox();

    /** Decay percent. */
    private JTextField tfSynpaseDecayPercent = new JTextField();

    /** Normalize inputs check box. */
    private JCheckBox cbNormalizeInputs = new JCheckBox();

    /** The model subnetwork. */
    private Competitive competitive;

    /** If true this is a creation panel. Otherwise it is an edit panel. */
    private boolean isCreationPanel;

    /**
     * Constructor for the case where a competitive network is being created.
     *
     * @param np parent network panel
     */
    public CompetitivePropertiesPanel(final NetworkPanel np) {
        this.networkPanel = np;
        isCreationPanel = true;
        mainPanel.addItem("Number of neurons", tfNumNeurons);
        initPanel();
    }

    /**
     * Constructor for the case where an existing competitive network is being
     * edited.
     *
     * @param np parent network panel
     * @param competitive Competitive network being modified.
     */
    public CompetitivePropertiesPanel(final NetworkPanel np,
            final Competitive competitive) {
        this.networkPanel = np;
        this.competitive = competitive;
        isCreationPanel = false;
        initPanel();
    }

    /**
     * Initialize the panel.
     */
    private void initPanel() {

        fillFieldValues();

        mainPanel.addItem("UpdateMethod", updateMethod);
        mainPanel.addItem("Epsilon", tfEpsilon);
        mainPanel.addItem("Winner Value", tfWinnerValue);
        mainPanel.addItem("Loser Value", tfLoserValue);
        mainPanel.addItem("Use Leaky Learning", cbUseLeakyLearning);
        mainPanel.addItem("Leaky Epsilon", tfLeakyEpsilon);
        mainPanel.addItem("Normalize Inputs", cbNormalizeInputs);
        mainPanel.addItem("Synapse Decay Percent", tfSynpaseDecayPercent);

        updateMethod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableFieldBasedOnUpdateMethod();
            }
        });
        cbUseLeakyLearning.addActionListener(this);
        cbUseLeakyLearning.setActionCommand("useLeakyLearning");

        checkLeakyEpsilon();
        enableFieldBasedOnUpdateMethod();

        add(mainPanel);
    }

    @Override
    public Group commitChanges() {
        if (isCreationPanel) {
            competitive = new Competitive(networkPanel.getNetwork(),
                    Integer.parseInt(tfNumNeurons.getText()));
        }
        competitive.setUpdateMethod((UpdateMethod) updateMethod
                .getSelectedItem());
        competitive.setLearningRate(Double.parseDouble(tfEpsilon.getText()));
        competitive.setWinValue(Double.parseDouble(tfWinnerValue.getText()));
        competitive.setLoseValue(Double.parseDouble(tfLoserValue.getText()));
        competitive.setSynpaseDecayPercent(Double
                .parseDouble(tfSynpaseDecayPercent.getText()));
        competitive.setLeakyLearningRate(Double.parseDouble(tfLeakyEpsilon
                .getText()));
        competitive.setUseLeakyLearning(cbUseLeakyLearning.isSelected());
        competitive.setNormalizeInputs(cbNormalizeInputs.isSelected());
        return competitive;
    }

    @Override
    public void fillFieldValues() {

        // For creation panels use an "empty" competitive network to harvest
        // default values
        if (isCreationPanel) {
            competitive = new Competitive(null, 1);
            tfNumNeurons.setText("" + DEFAULT_NUM_NEURONS);
        }
        updateMethod.setSelectedItem(competitive.getUpdateMethod());
        tfEpsilon.setText(Double.toString(competitive.getLearningRate()));
        tfLoserValue.setText(Double.toString(competitive.getLoseValue()));
        tfWinnerValue.setText(Double.toString(competitive.getWinValue()));
        tfLeakyEpsilon.setText(Double.toString(competitive
                .getLeakyLearningRate()));
        tfSynpaseDecayPercent.setText(Double.toString(competitive
                .getSynpaseDecayPercent()));
        cbUseLeakyLearning.setSelected(competitive.getUseLeakyLearning());
        cbNormalizeInputs.setSelected(competitive.getNormalizeInputs());
    }

    /**
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(final ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("useLeakyLearning")) {
            checkLeakyEpsilon();
        }

    }

    /**
     * Does what it says :)
     */
    private void enableFieldBasedOnUpdateMethod() {
        if (updateMethod.getSelectedItem() == Competitive.UpdateMethod.ALVAREZ_SQUIRE) {
            tfSynpaseDecayPercent.setEnabled(true);
        } else if (updateMethod.getSelectedItem() == Competitive.UpdateMethod.RUMM_ZIPSER) {
            tfSynpaseDecayPercent.setEnabled(false);
        }
    }

    /**
     * Checks whether or not to enable leaky epsilon.
     */
    private void checkLeakyEpsilon() {
        if (cbUseLeakyLearning.isSelected()) {
            tfLeakyEpsilon.setEnabled(true);
        } else {
            tfLeakyEpsilon.setEnabled(false);
        }
    }

    @Override
    public String getHelpPath() {
        return "Pages/Network/network/competitivenetwork.html";
    }

}