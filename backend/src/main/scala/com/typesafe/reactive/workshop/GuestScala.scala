package com.typesafe.reactive.workshop

import java.util.concurrent.TimeUnit

import akka.actor._
import com.typesafe.reactive.workshop.Guest.DrinkFinished
import com.typesafe.reactive.workshop.GuestScala.DrunkException
import com.typesafe.reactive.workshop.WaiterScala.DrinkServed

import scala.concurrent.duration.FiniteDuration


class GuestScala(waiter:ActorRef, favDrink:Drink, max : Int) extends Actor with ActorLogging {
  import context._

  waiter ! new WaiterScala.ServeDrink(favDrink)
  var dc = 0
  val finishCoffeeDuration = 2
  def receive = {
    case x:DrinkServed => {
      dc += 1
      log.info(s"Enjoying my ${dc}, yummy ${favDrink}")
      scheduleCoffeeFinished()
    }
    case GuestScala.DrinkFinished() if dc > max => throw new DrunkException
    case GuestScala.DrinkFinished() => waiter ! new WaiterScala.ServeDrink(favDrink)
    case x:GuestScala.DrunkException => log.info("Cannot take it anymore")
    case _ => {
      log.info("NOTHING")
    }
  }

  def scheduleCoffeeFinished() = {
    context.system.scheduler.scheduleOnce(FiniteDuration(2L,TimeUnit.SECONDS),self,new GuestScala.DrinkFinished())
  }
}

object GuestScala {
  def props(waiter:ActorRef, favDrink:Drink, maxDrinkCount: Int) = Props(new GuestScala(waiter,favDrink, maxDrinkCount))
  case class DrinkFinished()
  case class DrunkException() extends IllegalStateException
}
