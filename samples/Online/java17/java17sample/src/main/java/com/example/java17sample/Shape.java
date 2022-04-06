package com.example.java17sample;

import java.awt.Point;

sealed interface Shape {
	record Circle(Point center, int radius) implements Shape{}
	record Rectangle(Point lowerleft, Point upperRight) implements Shape{}

}
