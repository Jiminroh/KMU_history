#include "snake.h"
#include <cmath>
#include <unistd.h>

// @author 송영진(20160176)
snake_part::snake_part(int col, int row) {
    x = col;
    y = row;
}
// @author 송영진(20160176)
snake_part::snake_part() {
    x = 0;
    y = 0;
}

snake_class::snake_class() {
    initscr();
    nodelay(stdscr, true);
    keypad(stdscr, true);
    noecho();
    curs_set(0);
    getmaxyx(stdscr, max_height, max_width);

    // init color @author 노지민(20171617)
    start_color();
    init_pair(1, COLOR_YELLOW, COLOR_YELLOW); // snake head color
    init_pair(2, COLOR_BLUE, COLOR_BLUE); // snake tail color
    init_pair(3, COLOR_GREEN, COLOR_GREEN); // food color
    init_pair(4, COLOR_MAGENTA, COLOR_MAGENTA); // poison color
    init_pair(5, COLOR_RED, COLOR_RED); // gate color
    init_pair(6, COLOR_BLACK, COLOR_WHITE); // background color
    init_pair(7, COLOR_WHITE, COLOR_BLACK); // wall color


    attron(COLOR_PAIR(6));
    wbkgd(stdscr, COLOR_PAIR(6));
    attroff(COLOR_PAIR(6));

    //init a few variables @author 송영진(20160176) 50%, 노지민(20171617) 50%
    part_char = '4';
    oldal_char = (char)219; // wall
    immune_char = (char)64; // immune wall
    foo = '5';
    pos = '6';
    door = '7';
    food.x = 0;
    food.y = 0;
    poison.x = 0;
    poison.y = 0;
    gate1.x = 0;
    gate1.y = 0;
    gate2.x = 0;
    gate2.y = 0;
    food_item = 0;
    poison_item = 0;
    food_time = 0;
    gate = 0;
    del = 110000;
    food_get = false;
    poison_get = false;
    gate_get = false;
    is_ten = false;
    hit_wall = false;
    direction = 'l';

    // make snake @author 노지민(20171617)
    for (int i = 0; i < 3; i++) {
        snake.push_back(snake_part(40 + i, 10));
    }

    // make food & poison @author 송영진(20160176)
    srand(time(0));
    put_food();
    put_poison();

    // make wall @author 송영진(20160176)
    create_wall();

    // make gate @author 노지민(20171617)
    open_gate();

    // make snake @author 송영진(20160176) 50%, 노지민(20171617) 50%
    for (int i = 0; i < snake.size(); i++) {
        if (i == 0) {
            attron(COLOR_PAIR(1));
            move(snake[i].y, snake[i].x);
            addch('3');
            attroff(COLOR_PAIR(1));
        }
        else {
            attron(COLOR_PAIR(2));
            addch(part_char);
            attroff(COLOR_PAIR(2));
        }
    }
    // Score board @author 송영진(20160176)
    move(0, max_width - 11);
    printw("Score board");
    // Snake length
    move(1, max_width - 11);
    printw("B: %d", snake.size() + 1);
    // food item
    move(2, max_width - 11);
    printw("+: %d", food_item);
    // poison item
    move(3, max_width - 11);
    printw("-: %d", poison_item);
    // gate
    move(4, max_width - 11);
    printw("G: %d", gate);
    // Mission
    move(8, max_width - 11);
    printw("Mission");
    move(9, max_width - 11);
    printw("B: 10 ()");
    move(10, max_width - 11);
    printw("+: 5 ()");
    move(11, max_width - 11);
    printw("-: 2 ()");
    move(12, max_width - 11);
    printw("G: 1 ()");
    refresh();
}
// @author 송영진(20160176)
snake_class::~snake_class() {
    nodelay(stdscr, false);
    getch();
    endwin();
}

// @author 송영진(20160176)
void snake_class::create_wall() {
    attron(COLOR_PAIR(7));
    // up
    for (int i = 0; i < max_width - 11; i++) {
        move(0, i);
        wall.push_back(snake_part(0, i));
        addch(oldal_char);
    }
    // down
    for (int i = 0; i < max_width - 11; i++) {
        move(max_height - 1, i);
        wall.push_back(snake_part(max_height - 1, i));
        addch(oldal_char);
    }
    // left
    for (int i = 0; i < max_height; i++) {
        if (i == 0 || i == max_height - 1) {
            move(i, 0);
            addch(immune_char);
            continue;
        }
        else {
            move(i, 0);
            wall.push_back(snake_part(i, 0));
            addch(oldal_char);
        }
    }
    // right
    for (int i = 0; i < max_height; i++) {
        if (i == 0 || i == max_height - 1) {
            move(i, max_width - 12);
            addch(immune_char);
            continue;
        }
        else {
            wall.push_back(snake_part(i, max_width - 12));
            move(i, max_width - 12);
            addch(oldal_char);
        }
    }
    attroff(COLOR_PAIR(7));
}
// @author 노지민(20171617)
void snake_class::open_gate() {
    int idx1, idx2;
    while (1) {
        idx1 = rand() % wall.size();
        idx2 = rand() % wall.size();
        if (idx1 != idx2) {
            break;
        }
    }
    gate1.x = wall[idx1].x;
    gate1.y = wall[idx1].y;
    gate2.x = wall[idx2].x;
    gate2.y = wall[idx2].y;

    std::vector<snake_part>::iterator it1 = wall.begin();
    std::vector<snake_part>::iterator it2 = wall.begin();
    it1 += idx1;
    it2 += idx2;

    wall.erase(it1);
    wall.erase(it2);

    attron(COLOR_PAIR(5));
    move(gate1.x, gate1.y);
    addch(door);
    move(gate2.x, gate2.y);
    addch(door);
    attroff(COLOR_PAIR(5));
    refresh();
}
// @author 송영진(20160176)
void snake_class::put_food() {
    while (1) {
        int tmp_x = rand() % max_width + 1;
        int tmp_y = rand() % max_height + 1;
        for (int i = 0; i < snake.size(); i++) {
            if (snake[i].x == tmp_x && snake[i].y == tmp_y) {
                continue;
            }
        }
        if (tmp_x >= max_width - 12 || tmp_y >= max_height - 1) {
            continue;
        }
        food.x = tmp_x;
        food.y = tmp_y;
        break;
    }
    move(food.y, food.x);
    attron(COLOR_PAIR(3));
    addch(foo);
    attroff(COLOR_PAIR(3));
    refresh();
}
// @author // @author 노지민(20171617)
void snake_class::put_poison() {
    while (1) {
        int tmp_x = rand() % max_width + 1;
        int tmp_y = rand() % max_height + 1;
        for (int i = 0; i < snake.size(); i++) {
            if (snake[i].x == tmp_x && snake[i].y == tmp_y) {
                continue;
            }
        }
        if (tmp_x >= max_width - 12 || tmp_y >= max_height - 1) {
            continue;
        }
        poison.x = tmp_x;
        poison.y = tmp_y;
        break;
    }
    move(poison.y, poison.x);
    attron(COLOR_PAIR(4));
    addch(pos);
    attroff(COLOR_PAIR(4));
    refresh();
}
// @author 송영진(20160176) 50%, 노지민(20171617) 50%
bool snake_class::collision() {
    if (gate1.y == snake[0].x && gate1.x == snake[0].y) {
        pass_gate1 = true;
    }
    if (gate2.y == snake[0].x && gate2.x == snake[0].y) {
        pass_gate2 = true;
    }
    // hit wall
    for (int i = 0; i < wall.size(); i++) {
        if (wall[i].y == snake[0].x && wall[i].x == snake[0].y) {
            return true;
        }
    }
    // hit snake
    for (int i = 2; i < snake.size(); i++) {
        if (snake[0].x == snake[i].x && snake[i].y == snake[0].y) {
            return true;
        }
    }
    // min(snake.size) > 2
    if (snake.size() <= 2) {
        return true;
    }
    // eat food
    if (snake[0].x == food.x && snake[0].y == food.y) {
        food_get = true;
        put_food();
        food_item += 1;
        if (food_item == 5) {
            move(10, max_width - 11);
            printw("+: 5 (V)");
        }
        move(2, max_width - 11);
        printw("+: %d", food_item);
        if (snake.size() + 1 == 10) {
            is_ten = false;
        }
    }
    // eat poison
    else if (snake[0].x == poison.x && snake[0].y == poison.y) {
        poison_get = true;
        put_poison();
        poison_item += 1;

        if (poison_item == 2) {
            move(11, max_width - 11);
            printw("-: 2 (V)");
        }
        move(3, max_width - 11);
        printw("-: %d", poison_item);
    }
    // into gate
    else if (snake[0].x == gate1.x && snake[0].y == gate1.y || snake[0].x == gate2.x && snake[0].y == gate2.y) {
        gate_get = true;
        return false;
    }
    else {
        food_get = false;
        poison_get = false;
        gate_get = false;
    }
    return false;
}

void snake_class::move_snake() {
    int tmp = getch();
    // direction @author 송영진(20160176)
    switch (tmp) {
    case KEY_LEFT:
        if (direction != 'r') {
            direction = 'l';
        }
        else {
            direction = 'q';
        }
        break;
    case KEY_UP:
        if (direction != 'd') {
            direction = 'u';
        }
        else {
            direction = 'q';
        }
        break;
    case KEY_DOWN:
        if (direction != 'u') {
            direction = 'd';
        }
        else {
            direction = 'q';
        }
        break;
    case KEY_RIGHT:
        if (direction != 'l') {
            direction = 'r';
        }
        else {
            direction = 'q';
        }
        break;
    case KEY_BACKSPACE:
        direction = 'q';
        break;
    }
    // enter gate1 @author 노지민(20171617)
    if (pass_gate1 == true) {
        gate += 1;
        move(4, max_width - 11);
        printw("G: %d", gate);
        move(12, max_width - 11);
        printw("G: 1 (V)");
        int temp = snake.size();
        for (int i = 0; i < snake.size(); i++) {
            move(snake[i].y, snake[i].x);
            addch(' ');
        }
        if (gate2.x == 0) {
            direction = 'd';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate2.y, gate2.x - i));
            }
        }
        else if (gate2.x == max_height - 1) {
            direction = 'u';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate2.y, gate2.x + i));
            }
        }
        else if (gate2.y == max_width - 12) {
            direction = 'l';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate2.y + i, gate2.x));
            }
        }
        else if (gate2.y == 0) {
            direction = 'r';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate2.y - i, gate2.x));
            }
        }

        pass_gate1 = false;
    }
    // enter gate2 @author 노지민(20171617)
    if (pass_gate2 == true) {
        gate += 1;
        move(4, max_width - 11);
        printw("G: %d", gate);
        move(12, max_width - 11);
        printw("G: 1 (V)");
        int temp = snake.size();
        for (int i = 0; i < snake.size(); i++) {
            move(snake[i].y, snake[i].x);
            addch(' ');
        }

        if (gate1.x == 0) {
            direction = 'd';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate1.y, gate1.x - i));
            }
        }
        else if (gate1.x == max_height - 1) {
            direction = 'u';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate1.y, gate1.x + i));
            }
        }
        else if (gate1.y == max_width - 12) {
            direction = 'l';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate1.y + i, gate1.x));
            }
        }
        else if (gate1.y == 0) {
            direction = 'r';
            snake.clear();
            for (int i = 0; i < temp; i++) {
                snake.push_back(snake_part(gate1.y - i, gate1.x));
            }
        }

        pass_gate2 = false;
    }
    // eat poison @author 송영진(20160176)
    if (poison_get) {
        move(snake[snake.size() - 1].y, snake[snake.size() - 1].x);
        addch(' ');
        move(snake[snake.size() - 2].y, snake[snake.size() - 2].x);
        addch(' ');
        move(snake[0].y, snake[0].x);
        attron(COLOR_PAIR(1));
        addch('3');
        attroff(COLOR_PAIR(1));
        refresh();
        snake.pop_back();
        snake.pop_back();

        if (snake.size() + 1 == 9) {
            is_ten = true;
            move(1, max_width - 11);
            printw("B:%d", snake.size() + 1);
        }
        else {
            move(1, max_width - 11);
            printw("B: %d", snake.size() + 1);
        }


    }
    // eat food @author 노지민(20171617)
    if (!food_get && !poison_get) {
        move(snake[snake.size() - 1].y, snake[snake.size() - 1].x);
        addch(' ');
        move(snake[0].y, snake[0].x);
        attron(COLOR_PAIR(1));
        addch('3');
        attroff(COLOR_PAIR(1));
        refresh();
        snake.pop_back();

        if (snake.size() + 1 == 10) {
            move(9, max_width - 11);
            printw("B: 10 (V)");
        }
        if (is_ten == true) {
            move(1, max_width - 11);
            printw("B: 0%d", snake.size() + 1);
        }
        else {
            move(1, max_width - 11);
            printw("B: %d", snake.size() + 1);
        }
    }
    if (direction == 'l')
        snake.insert(snake.begin(), snake_part(snake[0].x - 1, snake[0].y));
    else if (direction == 'r')
        snake.insert(snake.begin(), snake_part(snake[0].x + 1, snake[0].y));
    else if (direction == 'u')
        snake.insert(snake.begin(), snake_part(snake[0].x, snake[0].y - 1));
    else if (direction == 'd')
        snake.insert(snake.begin(), snake_part(snake[0].x, snake[0].y + 1));

    attron(COLOR_PAIR(5));
    move(gate1.x, gate1.y);
    addch(door);
    move(gate2.x, gate2.y);
    addch(door);
    attroff(COLOR_PAIR(5));

    move(snake[0].y, snake[0].x);
    attron(COLOR_PAIR(1));
    addch('3');
    attroff(COLOR_PAIR(1));

    move(snake[1].y, snake[1].x);
    attron(COLOR_PAIR(2));
    addch(part_char);
    attroff(COLOR_PAIR(2));
    refresh();
}
// @author 노지민(20171617)
void snake_class::erase() {
    move(food.y, food.x);
    addch(' ');
}
// @author 노지민(20171617)
void snake_class::add_wall() {
    attron(COLOR_PAIR(7));
    for (int i = 6; i < max_height / 2; i++) {
        move(i, (max_width - 12) / 2);
        wall.push_back(snake_part(i, (max_width - 12) / 2));
        addch(oldal_char);
    }
    for (int i = 12; i < (max_width - 12) / 2 + 1; i++) {
        move(max_height / 2, i);
        wall.push_back(snake_part(max_height / 2, i));
        addch(oldal_char);
    }
    attroff(COLOR_PAIR(7));
}
// @author 송영진(20160176) 50%, 노지민(20171617) 50%
void snake_class::start() {
    double timer = 0;

    while (1) {
        food_time += 0.1;
        timer += 0.1;
        move(16, max_width - 11);
        printw("Time: %0.f", floor(timer));

        if (floor(timer) == 30) {
            add_wall();
        }
        if (floor(food_time) == 5) {
            erase();
            put_food();
            food_time = 0;
        }

        napms(10);
        refresh();

        if (collision()) {
            move(max_height / 2 - 1, max_width / 2 - 9);
            printw("game over");
            break;
        }

        move_snake();

        if (direction == 'q') {
            printw("game over");
            break;
        }

        usleep(del);
    }

}




