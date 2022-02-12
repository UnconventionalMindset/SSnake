package io.github.jacopogobbi.ssnake

import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

import scala.collection.mutable
import scala.util.Random

object SSnake extends JFXApp3:
  val rand = new Random()
  val initialSpeed = 3
  val foodColor: IntegerProperty = IntegerProperty(0)
  val w = 20
  val h = 20
  val cornerSize = 40

  val speed: IntegerProperty = IntegerProperty(initialSpeed)
  var foodX = 0
  var foodY = 0
  val direction: ObjectProperty[Direction] = ObjectProperty[Direction](Direction.Left)
  var gameOver: BooleanProperty = BooleanProperty(false)
  var snake: mutable.Seq[Corner] = mutable.Seq()

  def newFood(): Unit =
    foodX = rand.nextInt(w)
    foodY = rand.nextInt(h)

    snake.foreach { segment =>
      if segment.x.value == foodX && segment.y.value == foodY then
        newFood()
    }
    foodColor.value = rand.nextInt(5)
    speed.value += 1

  def tick(gc: GraphicsContext): Unit =
    if gameOver.value then
      gc.fill = Color.Red
      gc.font = Font("", 50)
      gc.fillText("Game Over", 100, 250)
    else
      (snake.size - 1 to 1 by -1).foreach { i =>
        snake(i).x.value = snake(i - 1).x.value
        snake(i).y.value = snake(i - 1).y.value
      }
      direction.value match
        case Direction.Up =>
          snake.head.y.value -= 1
          if snake.head.y.value < 0 then
            gameOver.value = true
        case Direction.Down =>
          snake.head.y.value += 1
          if snake.head.y.value > h then
            gameOver.value = true
        case Direction.Left =>
          snake.head.x.value -= 1
          if snake.head.x.value < 0 then
            gameOver.value = true
        case Direction.Right =>
          snake.head.x.value += 1
          if snake.head.y.value > w then
            gameOver.value = true

      if foodX == snake.head.x.value && foodY == snake.head.y.value then
        snake = snake :+ Corner(IntegerProperty(-1), IntegerProperty(1))
        newFood()

      (1 until snake.size).foreach { i =>
        if snake.head.x.value == snake(i).x.value && snake.head.y.value == snake(i).y.value then
          gameOver.value = true
      }

      gc.fill = Color.Black
      gc.fillRect(0, 0, w * cornerSize, h * cornerSize)

      gc.fill = Color.White
      gc.font = Font("", 30)
      gc.fillText("Score: " + (speed.value - initialSpeed), 10, 30)

      gc.fill = foodColor.value match
        case 0 => Color.Purple
        case 1 => Color.LightBlue
        case 2 => Color.Yellow
        case 3 => Color.Pink
        case 4 => Color.Orange

      gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize)

      snake.foreach { segment =>
        gc.fill = Color.LightGreen
        gc.fillRect(segment.x.value * cornerSize, segment.y.value * cornerSize, cornerSize - 1, cornerSize - 1)
        gc.fill = Color.Green
        gc.fillRect(segment.x.value * cornerSize, segment.y.value * cornerSize, cornerSize - 1, cornerSize - 1)
      }

  override def start(): Unit =
    newFood()
    var gc: GraphicsContext = null
    val oneSec = 1_000_000_000L
    var lastTick = 0L
    val timer: AnimationTimer = AnimationTimer(now => {
      if lastTick == 0L || (now - lastTick > oneSec / speed.value) then {
        lastTick = now
        tick(gc)
      }
    })

    stage = new PrimaryStage {
      title = "Scala Snake"
      width = w * cornerSize
      height = h * cornerSize
      scene = new Scene {
        val rootPane = new BorderPane
        rootPane.top = new MenuBar {
          menus = List {
            new Menu("File") {
              items = List {
                new MenuItem("New Game") {
                  onAction = { _ =>
                    gameOver.value = false
                    newFood()
                    timer.start()
                    snake = mutable.ArrayDeque[Corner](
                      Corner(IntegerProperty(w / 2), IntegerProperty(h / 2)),
                      Corner(IntegerProperty(w / 2), IntegerProperty(h / 2)),
                      Corner(IntegerProperty(w / 2), IntegerProperty(h / 2)),
                    )
                  }
                }
              }
            }
          }
        }
        rootPane.center = new HBox(1) {
          children = new Canvas {
            width <== rootPane.width
            height <== rootPane.height
            gc = this.graphicsContext2D
          }
        }
        root = rootPane
        onKeyPressed = (ev: KeyEvent) => {
          ev.code match
            case KeyCode.A | KeyCode.Left if direction.value != Direction.Right =>
              direction.value = Direction.Left
            case KeyCode.D | KeyCode.Right if direction.value != Direction.Left =>
              direction.value = Direction.Right
            case KeyCode.S | KeyCode.Down if direction.value != Direction.Up =>
              direction.value = Direction.Down
            case KeyCode.W | KeyCode.Up if direction.value != Direction.Down =>
              direction.value = Direction.Up
            case _ =>
        }
      }
    }