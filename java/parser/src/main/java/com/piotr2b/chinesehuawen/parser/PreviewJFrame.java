/* 
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 Gephi is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 Gephi is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.piotr2b.chinesehuawen.parser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JFrame;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.scale.Contract;
import org.gephi.layout.plugin.scale.ScaleLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import processing.core.PApplet;

/**
 *
 * @author caocoa
 */
public class PreviewJFrame {

	private Set<Node> set;

	private PreviewJFrame() {
	}

	public PreviewJFrame(Set<Node> set) {
		this.set = set;
	}

	public void script() {
		// Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();

		// Get a graph model - it exists because we have a workspace
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

		// Append as a Directed Graph
		DirectedGraph directedGraph = graphModel.getDirectedGraph();

		HashMap<Integer, org.gephi.graph.api.Node> indexTranslation = new HashMap<Integer, org.gephi.graph.api.Node>(); // translation

		set.stream().flatMap(node -> node.getNodeSet().stream()).distinct().forEach(x -> {
			System.out.println(" Sinogram " + x);
			if (!indexTranslation.containsKey(x.getId())) {
				org.gephi.graph.api.Node n = graphModel.factory().newNode(Integer.toString(x.getId()));
				n.getNodeData().setLabel(x.toString());
				indexTranslation.put(x.getId(), n);
				directedGraph.addNode(n);
			}
			x.leaves.forEach(y -> {
				if (!indexTranslation.containsKey(y.getId())) {
					org.gephi.graph.api.Node n = graphModel.factory().newNode(Integer.toString(y.getId()));
					n.getNodeData().setLabel(y.toString());
					indexTranslation.put(y.getId(), n);
					directedGraph.addNode(n);
				}

				org.gephi.graph.api.Node n1 = indexTranslation.get(x.getId());
				org.gephi.graph.api.Node n2 = indexTranslation.get(y.getId());
				org.gephi.graph.api.Edge e = graphModel.factory().newEdge(n1, n2, 1f, true);
				directedGraph.addEdge(e);
			});
		});

		InputStream istream = Main.class.getResourceAsStream("/Unifont.ttf");
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, istream);
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		font = font.deriveFont(75f);

		YifanHuLayout yifanHuLayout = new YifanHuLayout(null, new StepDisplacement(1f));
		yifanHuLayout.setGraphModel(graphModel);
		yifanHuLayout.initAlgo();
		yifanHuLayout.resetPropertiesValues();
		yifanHuLayout.setOptimalDistance(200f);

		for (int i = 0; i < 10 && yifanHuLayout.canAlgo(); i++) {
			yifanHuLayout.goAlgo();
		}
		yifanHuLayout.endAlgo();

		ScaleLayout layout = (new Contract()).buildLayout();
		layout.setGraphModel(graphModel);
		layout.initAlgo();
		layout.resetPropertiesValues();

		for (int i = 0; i < 5 && layout.canAlgo(); i++) {
			layout.goAlgo();
		}
		layout.endAlgo();

		// Preview configuration
		PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
		PreviewModel previewModel = previewController.getModel();
		previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
		previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
		// previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_SIZE,
		// 12f);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, font);
		previewController.refreshPreview();

		// New Processing target, get the PApplet
		ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		PApplet applet = target.getApplet();
		applet.init();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Refresh the preview and reset the zoom
		previewController.render(target);
		target.refresh();
		target.resetZoom();

		// Add the applet to a JFrame and display
		JFrame frame = new JFrame("Chinese-huawen");
		frame.setLayout(new BorderLayout());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(applet, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}
}