# Fractal Explorer (Java)

An interactive Mandelbrot fractal explorer written in **pure Java (Swing)**.
This project renders the Mandelbrot set and allows real-time exploration through zooming, panning, and animated color palettes.

The renderer uses **multithreaded computation** to generate fractals efficiently and supports smooth navigation across the complex plane.

---

## Demo

![Fractal explorer Demo](fractal.gif)

---

## Features

* Interactive **Mandelbrot set visualization**
* **Mouse-wheel zoom** centered at the cursor
* **Click and drag panning**
* **Multithreaded rendering** using all available CPU cores
* **Animated color palette cycling**
* Real-time fractal exploration

---

## Controls

| Action        | Control      |
| ------------- | ------------ |
| Zoom in / out | Mouse Wheel  |
| Move around   | Click + Drag |

---

## How It Works

The Mandelbrot set is defined by the iterative equation:

```
z = z² + c
```

Where:

* `z` starts at `0`
* `c` is the complex coordinate corresponding to a pixel

For each pixel:

1. Map the pixel to a complex number.
2. Iterate the Mandelbrot equation.
3. Stop if `|z| > 2` (escape condition) or the maximum iteration limit is reached.
4. The number of iterations determines the pixel color.

Points that never escape belong to the **Mandelbrot set** and are rendered in black.

---

## Multithreaded Rendering

To improve performance, the image is split into horizontal sections and computed in parallel.

```
Thread 1 → rows 0–100
Thread 2 → rows 100–200
Thread 3 → rows 200–300
...
```

This allows the program to utilize **all CPU cores**, making zooming and navigation significantly faster.

---

## Project Structure

```
src/
 ├── Main.java
 ├── FractalPanel.java
 └── Mandelbrot.java
```

* **Main.java** – creates the application window
* **FractalPanel.java** – rendering, interaction, multithreading
* **Mandelbrot.java** – Mandelbrot iteration logic

---

## Technologies Used

* Java
* Java Swing
* Multithreading
* Complex number mathematics

---

## Future Improvements

Possible extensions for the explorer:

* Julia set mode
* Smooth coloring algorithms
* GPU acceleration
* Progressive rendering
* Export high-resolution fractal images

---


