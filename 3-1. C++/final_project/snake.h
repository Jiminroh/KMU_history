#include <ncurses.h>
#include <iostream>
#include <vector>
#include <cstdlib>
#ifndef SNAKE_H
#define SNAKE_H

using namespace std;

struct snake_part{
    int x, y;
    snake_part(int col, int row);
    snake_part();
};

class snake_class{
    int del, max_width, max_height, food_item, poison_item, gate;
    double food_time;
    char direction, part_char, oldal_char, foo, pos, immune_char, door;
    bool food_get, poison_get, is_ten, gate_get, hit_wall, pass_gate1, pass_gate2;
    snake_part food, poison, gate1, gate2;
    vector<snake_part> snake, wall;

    void init();
    void create_wall();
    void put_food();
    void put_poison();
    bool collision();
    void move_snake();
    void open_gate();
    void erase();
    void add_wall();


public:
    snake_class();
    ~snake_class();
    void start();
};

#endif
