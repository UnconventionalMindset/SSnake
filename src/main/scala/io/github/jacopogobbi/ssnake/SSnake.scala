package io.github.jacopogobbi.ssnake

import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.text.Font

import scala.collection.mutable
import scala.util.Random

object SSnake extends JFXApp {
  val rand = new Random()
  val initialSpeed = 3
  var foodColor = 0
  val w = 20
  val h = 20
  val cornerSize = 50

  var speed: Int = initialSpeed
  var foodX = 0
  var foodY = 0
  var direction: Direction = Direction.Left
  var gameOver = false
  var snake: mutable.Seq[Corner] = mutable.Seq()

  def newFood(): Unit = {
    foodX = rand.nextInt(w)
    foodY = rand.nextInt(h)

    snake.foreach { c =>
      if (c.x == foodX && c.y == foodY) {
        return newFood()
      }
    }
    foodColor = rand.nextInt(5)
    speed += 1
  }

  def tick(gc: GraphicsContext): Unit = {
    if (gameOver) {
      gc.fill = Color.Red
      gc.font = Font("", 50)
      gc.fillText("Game Over", 100, 250)

      return
    }
    for (i <- snake.size-1 to 1 by -1) {
      val previousLocation = snake(i - 1)
      snake(i) = snake(i).copy(previousLocation.x, previousLocation.y)
    }
    direction match {
      case Direction.Up =>
        snake.head.y = snake.head.y - 1
        if (snake.head.y < 0) gameOver = true
      case Direction.Down =>
        snake.head.y = snake.head.y + 1
        if (snake.head.y > h) gameOver = true
      case Direction.Left =>
        snake.head.x = snake.head.x - 1
        if (snake.head.x < 0) gameOver = true
      case Direction.Right =>
        snake.head.x = snake.head.x + 1
        if (snake.head.y > w) gameOver = true
    }

    if (foodX == snake.head.x && foodY == snake.head.y) {
      snake = snake :+ Corner(-1, 1)
      newFood()
    }
    (1 until snake.size).foreach { i =>
      if (snake.head.x == snake(i).x && snake.head.y == snake(i).y) {
        gameOver = true
      }
    }

    gc.fill = Color.Black
    gc.fillRect(0, 0, w * cornerSize, h * cornerSize)

    gc.fill = Color.White
    gc.font = Font("", 30)
    gc.fillText("Score: " + (speed - initialSpeed), 10, 30)

    var cc = Color.White

    foodColor match {
      case 0 => cc = Color.Purple
      case 1 => cc = Color.LightBlue
      case 2 => cc = Color.Yellow
      case 3 => cc = Color.Pink
      case 4 => cc = Color.Orange
    }

    gc.fill = cc
    gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize)

    snake.foreach { c =>
      gc.fill = Color.LightGreen
      gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1)
      gc.fill = Color.Green
      gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1)
    }
  }

  newFood()

  var gc: GraphicsContext = _

  stage = new PrimaryStage {
    title = "SSnake"
    width = w * cornerSize
    height = h * cornerSize
    scene = new Scene(w * cornerSize, h * cornerSize) {
      fill = Black
      onKeyPressed = (ev: KeyEvent) => {
        if (ev.code == KeyCode.W || ev.code == KeyCode.Up) direction = Direction.Up
        if (ev.code == KeyCode.A || ev.code == KeyCode.Left) direction = Direction.Left
        if (ev.code == KeyCode.S || ev.code == KeyCode.Down) direction = Direction.Down
        if (ev.code == KeyCode.D || ev.code == KeyCode.Right) direction = Direction.Right
      }
      content = new VBox {
        children = new Canvas(w * cornerSize, h * cornerSize) {
//          stylesheets = Seq(getClass.getResource("application.css").toExternalForm)
          gc = this.graphicsContext2D
        }
      }
    }
  }
  var lastTick = 0L
  val timer: AnimationTimer = AnimationTimer(now => {
    if (lastTick == 0L) {
      lastTick = now
      tick(gc)
    } else if (now - lastTick > 1_000_000_000L / speed) {
      lastTick = now
      tick(gc)
    }
  })
  timer.start
  snake = mutable.Seq(
    Corner(w / 2, h / 2),
    Corner(w / 2, h / 2),
    Corner(w / 2, h / 2)
  )
}