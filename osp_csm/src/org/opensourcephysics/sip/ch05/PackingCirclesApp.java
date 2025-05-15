package org.opensourcephysics.sip.ch05;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import org.opensourcephysics.display.*;
import java.awt.Graphics;
import java.util.Random;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;




public class PackingCirclesApp extends AbstractSimulation implements Drawable {

  DrawingPanel panel;
  DrawingFrame window;
  Particle[] particles;
  int N = 100;
  double boxWidth = 12;
  double boxHeight = 12;
  int gridSize = 5; // cell width/height (tune this)
  int cols, rows;
  ArrayList<Particle>[][] grid;


  public PackingCirclesApp() {
	  panel = new DrawingPanel();
	  window = new DrawingFrame(panel);
	  panel.setPreferredMinMax(0, 12, 0, 12);
	  initialize();

  }
  
  public void saveParticleStates(String filename) {
	  try {
	    PrintWriter writer = new PrintWriter(filename);
	    writer.println("x,y,vx,vy,radius");

	    for (Particle p : particles) {
	      writer.printf("%.4f,%.4f,%.4f,%.4f,%.4f%n", p.x, p.y, p.vx, p.vy, p.radius);
	    }

	    writer.close();
	    System.out.println("Particle states saved to " + filename);
	  } catch (Exception e) {
	    System.err.println("Failed to save particle states: " + e.getMessage());
	  }
	}


  /**
   * Initializes the simulation
   */
  public void initialize() {
	  Random rand = new Random();
	  particles = new Particle[N];

	  // ← First: initialize all particles
	  for (int i = 0; i < N; i++) {
	    double x = rand.nextDouble() * 10;
	    double y = rand.nextDouble() * 10;
	    double r = 0.2 + rand.nextDouble() * 0.3;
	    particles[i] = new Particle(x, y, r);
	  }

	  // ← Then: prepare the grid
	  cols = (int)(boxWidth / gridSize) + 1;
	  rows = (int)(boxHeight / gridSize) + 1;
	  grid = new ArrayList[cols][rows];
	  for (int i = 0; i < cols; i++) {
	    for (int j = 0; j < rows; j++) {
	      grid[i][j] = new ArrayList<Particle>();
	    }
	  }

	  // ← Then: add initialized particles to the grid
	  for (Particle p : particles) {
	    int col = (int)(p.x / gridSize);
	    int row = (int)(p.y / gridSize);
	    grid[col][row].add(p);
	  }

	  window.clearDrawables();
	  for (int i = 0; i < N; i++) {
	    panel.addDrawable(new VisualParticle(particles[i]));
	  }
	}

  
  
  public double computeBoundingBoxArea() {
	  double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
	  double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

	  for (Particle p : particles) {
	    minX = Math.min(minX, p.x - p.radius);
	    minY = Math.min(minY, p.y - p.radius);
	    maxX = Math.max(maxX, p.x + p.radius);
	    maxY = Math.max(maxY, p.y + p.radius);
	  }

	  double area = (maxX - minX) * (maxY - minY);
	  System.out.printf("Bounding box area: %.4f%n", area);
	  return area;
	}


  /**
   * One step in the simulation
   */
  public void computeForces() {
	  // Reset accelerations
	  for (int i = 0; i < N; i++) {
	    particles[i].ax = 0;
	    particles[i].ay = 0;
	  }

	  double G = 50.0;           // Gravity strength toward center
	  double repulsionK = 200.0; // Repulsion when too close
	  double epsilon = 0.01;

	  double centerX = boxWidth / 2.0;
	  double centerY = boxHeight / 2.0;

	  for (int i = 0; i < N; i++) {
	    Particle p = particles[i];

	    // Distance to center
	    double dx = centerX - p.x;
	    double dy = centerY - p.y;
	    double distSq = dx * dx + dy * dy + epsilon;
	    double dist = Math.sqrt(distSq);

	    double nx = dx / dist;
	    double ny = dy / dist;

	    // Gravity toward center
	    double Fgravity = G * p.mass / distSq;
	    p.ax += Fgravity * nx;
	    p.ay += Fgravity * ny;
	  }

	  // Optional: add repulsion between particles to avoid overlap
	  for (int col = 0; col < cols; col++) {
	    for (int row = 0; row < rows; row++) {
	      for (Particle pi : grid[col][row]) {
	        for (int dx = -1; dx <= 1; dx++) {
	          for (int dy = -1; dy <= 1; dy++) {
	            int nc = col + dx;
	            int nr = row + dy;
	            if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) continue;

	            for (Particle pj : grid[nc][nr]) {
	              if (pi == pj) continue;

	              double dx2 = pj.x - pi.x;
	              double dy2 = pj.y - pi.y;
	              double distSq = dx2 * dx2 + dy2 * dy2 + epsilon;
	              double dist = Math.sqrt(distSq);
	              double minDist = pi.radius + pj.radius;

	              if (dist < minDist) {
	                double overlap = minDist - dist;
	                double nx = dx2 / dist;
	                double ny = dy2 / dist;

	                double Frep = repulsionK * overlap;

	                // Apply repulsion
	                pi.ax -= Frep * nx / pi.mass;
	                pi.ay -= Frep * ny / pi.mass;
	                pj.ax += Frep * nx / pj.mass;
	                pj.ay += Frep * ny / pj.mass;
	              }
	            }
	          }
	        }
	      }
	    }
	  }
	}


  
  public void doStep() {

	  if (particles == null || grid == null) {
	    System.out.println("Simulation not initialized. Click 'Initialize' first.");
	    return;
	  }

	  double dt = 0.01;

	  // 1. Clear and reassign particles to grid
	  for (int i = 0; i < cols; i++) {
	    for (int j = 0; j < rows; j++) {
	      grid[i][j].clear();
	    }
	  }

	  for (Particle p : particles) {
	    int col = (int)(p.x / gridSize);
	    int row = (int)(p.y / gridSize);
	    if (col >= 0 && col < cols && row >= 0 && row < rows) {
	      grid[col][row].add(p);
	    }
	  }

	  // 2. Compute forces now that grid is ready
	  computeForces();

	  // 3. Position update using Verlet
	  for (int i = 0; i < N; i++) {
	    Particle p = particles[i];
	    p.x += p.vx * dt + 0.5 * p.ax * dt * dt;
	    p.y += p.vy * dt + 0.5 * p.ay * dt * dt;
	  }

	  // 4. Recompute forces at new positions
	  computeForces();

	  // 5. Velocity update and wall bouncing
	  for (int i = 0; i < N; i++) {
	    Particle p = particles[i];
	    p.vx += 0.5 * p.ax * dt;
	    p.vy += 0.5 * p.ay * dt;

	    if (p.x - p.radius < 0) {
	      p.x = p.radius;
	      p.vx *= -0.5;
	    }
	    if (p.x + p.radius > boxWidth) {
	      p.x = boxWidth - p.radius;
	      p.vx *= -0.5;
	    }
	    if (p.y - p.radius < 0) {
	      p.y = p.radius;
	      p.vy *= -0.5;
	    }
	    if (p.y + p.radius > boxHeight) {
	      p.y = boxHeight - p.radius;
	      p.vy *= -0.5;
	    }
	  }

	  panel.repaint();
	  saveParticleStates("particles_log.txt");
	  System.out.printf("Step bounding box area: %.4f%n", computeBoundingBoxArea());
	}




  /**
   * Optional global drawing (not needed here)
   */
  public void draw(DrawingPanel panel, Graphics g) {
    // Not needed since particles are handled by VisualParticle
  }

  /**
   * Reset the simulation to default values
   */
  public void reset() {
    control.setValue("N", 20);
    N = control.getInt("N");
    initialize();
  }

  /**
   * Main method to launch the simulation
   */
  public static void main(String[] args) {
    SimulationControl.createApp(new PackingCirclesApp());
  }
}

/**
 * Visual representation of a particle
 */
class VisualParticle implements Drawable {
  Particle p;

  public VisualParticle(Particle p) {
    this.p = p;
  }

  public void draw(DrawingPanel panel, Graphics g) {
    int px = panel.xToPix(p.x - p.radius);
    int py = panel.yToPix(p.y + p.radius);
    int d = (int)(2 * p.radius * panel.getXPixPerUnit());

    g.drawOval(px, py, d, d);
  }
}

/**
 * Basic particle structure
 */
class Particle {
  double x, y;       // position
  double vx = 0, vy = 0; // velocity
  double ax = 0, ay = 0; // acceleration
  double radius;
  double mass = 1;
  double density = 1.0; 


  public Particle(double x, double y, double r) {
	  this.x = x;
	  this.y = y;
	  this.radius = r;
	  this.density = 1.0;
	  this.mass = Math.PI * r * r * density; // for circular particles
	}

}
