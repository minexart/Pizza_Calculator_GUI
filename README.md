# 🍕 Pizza Calculator GUI

A Java Swing-based desktop application built as a coursework project for **Java Programming (Part 2)**. It helps you figure out exactly how many pizzas to order for a group — by headcount, hunger level, and pizza type — then displays an itemised cost breakdown and saves order history to a file.

## Features

- **Swing GUI** — clean graphical interface built with Java Swing components
- **Headcount input** — enter the number of people being catered for
- **Hunger level selector** — choose between Light, Medium, or Ravenous appetites
- **Pizza type dropdown** — pick from 8 pizza varieties with varying slice counts and prices
- **Add to order** — build a multi-pizza order by adding multiple rows to the results table
- **Running total** — live cost total updates as items are added
- **Save & reset** — saves the complete order to `order_history.txt` with a timestamp, then clears the form for a new order
- **Input validation** — catches non-numeric headcounts and empty selections with friendly error dialogues

## Pizza Menu

| Pizza | Slices | Price (RM) |
|-------|--------|-----------|
| Margherita | 8 | 18.00 |
| Pepperoni | 8 | 20.00 |
| Hawaiian | 8 | 21.00 |
| BBQ Chicken | 8 | 23.50 |
| Veggie Delight | 8 | 19.50 |
| Meat Lovers | 10 | 24.00 |
| Seafood | 10 | 26.00 |
| Supreme Deluxe | 12 | 28.50 |

## Hunger Levels

| Level | Slices per Person |
|-------|------------------|
| Light | 1 |
| Medium | 2 |
| Ravenous | 4 |

> Pizzas needed = ⌈ (people × slices per person) ÷ slices per pizza ⌉

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or later

### Compile & Run

```bash
javac PizzaCalculator.java
java PizzaCalculator
```

## How It Works

1. Launch the app — the Pizza Calculator window opens.
2. Enter the **number of people** in the text field.
3. Select a **hunger level** using the radio buttons.
4. Choose a **pizza type** from the dropdown.
5. Click **Add to Order** — a row is added to the table showing slices needed, pizzas needed, and cost.
6. Repeat for additional pizza types in the same order.
7. Click **Save Order & New** to write the full order to `order_history.txt` and reset the form.

## File Output

Orders are appended to `order_history.txt` in the working directory:

```
Order placed at: 2025-11-01 14:32:00
Pizza           Slices per Pizza  Price (RM)  People  Hunger Level  Slices Needed  Pizzas Needed  Cost (RM)
Pepperoni       8                 20.00       10      Medium        20             3              60.00
Meat Lovers     10                24.00       10      Medium        20             2              48.00
Total: RM 108.00
```

## Project Context

| | |
|---|---|
| **Course** | Java Programming (DJP2264N) — Part 2 |
| **Language** | Java |
| **UI Framework** | Java Swing |
| **Concepts practiced** | GUI programming, event listeners, JTable, file I/O, input validation, OOP |

## Author

**Aminat Olaide** ([@minexart](https://github.com/minexart))
