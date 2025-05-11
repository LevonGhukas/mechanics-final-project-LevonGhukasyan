# 🌀 2D Circle Packing Simulation — `PackingCirclesApp`

**Author:** Levon Ghukasyan  
**Course:** Mechanics Final Project, AUA (Spring 2025)

---

## 🎯 Objective

This project simulates **2D circle packing** using particle-based physics and interaction forces within a bounded area. Each circle is a particle that repels, attracts, and bounces within a box. Performance is optimized using a **grid-based spatial partitioning** strategy. Results are visualized and logged for further analysis.

---

## ⚙️ Physical Model

Each particle has:
- Position `(x, y)`
- Velocity `(vx, vy)`
- Acceleration `(ax, ay)`
- Radius and mass (density = 1)

### 🧲 Inter-particle Forces:
- **Repulsion** if overlapping  
  `F += k * (minDist - dist)`
- **Attraction** if within weak-range  
  `F -= a * (dist - minDist)`
- **Damping (air resistance)**  
  `F -= damping * v`

### ⛔ Wall Collision:
Elastic bounce:
```java
if (x < radius || x > width - radius) vx *= -0.5;
if (y < radius || y > height - radius) vy *= -0.5;

 Integration Algorithm
We use Verlet integration:
x += vx * dt + 0.5 * ax * dt * dt;
vx += 0.5 * ax * dt;

Each doStep():
Clear grid
Assign particles to cells
Compute forces
Update positions
Handle wall bounces
Save to file

Grid Optimization

Without optimization, force calculation is O(N²).
This project divides the simulation space into a grid:
Particles interact only with others in neighboring cells
Reduces complexity to ~O(N) per step

Output

📝 particles_log.txt (saved each step)
Each line:
x, y, vx, vy, radius
example:
2.34, 5.87, 0.00, 0.00, 0.25

Console Output: Bounding box area: 83.12


<img width="1315" alt="image" src="https://github.com/user-attachments/assets/70705466-61b5-4043-a464-cca32164e688" />


