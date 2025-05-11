# 🌀 Circle Packing Simulation — `PackingCirclesApp`

**Author:** Levon Ghukasyan  
**Course:** Mechanics Final Project, AUA (Spring 2025)

---

## 🎯 Objective

This project simulates **2D circle packing** using particle-based physics inside a bounded box.  
Each circle (particle) moves and interacts using spring-like forces, weak attraction, and damping.  
The simulation uses an **optimized grid structure** to accelerate force calculations.

---

## ⚙️ Model Overview

Each particle has:
- Position `(x, y)`
- Velocity `(vx, vy)`
- Acceleration `(ax, ay)`
- Radius and mass (mass = π·r² for density 1)

### Forces applied:
| Force Type   | Description                                          |
|--------------|------------------------------------------------------|
| Repulsion    | When circles overlap                                 |
| Attraction   | Between nearby particles within light range          |
| Damping      | Proportional to velocity (simulates air resistance)  |

### Wall handling:
Particles bounce off walls with damping (`vx *= -0.5`, `vy *= -0.5`)

---

## ⏱ Time Integration

Uses **Verlet integration** for stability:

```java
x += vx * dt + 0.5 * ax * dt * dt;
vx += 0.5 * ax * dt;
```

🔄 Grid Optimization

Force calculations are accelerated by dividing the simulation box into a grid.
Each particle only interacts with others in neighboring cells. This reduces the complexity from O(N²) to ~O(N) per step.
💾 Output

File: particles_log.txt
Generated at every simulation step.
Format:
```java
x, y, vx, vy, radius
```
Example:
```java 
2.35, 5.87, 0.00, 0.00, 0.25
```

Console Output
Also prints: 
```java
Bounding box area: 83.26
```

📈 Observations

Particles begin at random positions with zero velocity.
Over time, they interact and self-organize into more compact configurations.
Bounding box area decreases and stabilizes.
Grid optimization allows real-time performance even for 100+ particles.
🔬 Future Improvements

Add simulated annealing to minimize total energy.
Shrink box size over time for auto-compression.
Visualize density maps, pairwise distances, or contact graphs.
Export animation frames or generate .gif from states.
🛠 How to Run

Open in Eclipse or any Java IDE.
Run PackingCirclesApp.java.
Use GUI buttons:
Initialize to create particles and grid.
Start/Step to begin simulation.
Check particles_log.txt for results.

✅ Built for Mechanics Final Project
🧠 Powered by Open Source Physics + Java
Levon Ghukasyan
