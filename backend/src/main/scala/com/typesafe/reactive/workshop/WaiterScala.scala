package com.typesafe.reactive.workshop

import akka.actor.{ActorLogging, Actor, ActorRef, Props}
import akka.routing.RoundRobinPool
import com.typesafe.reactive.workshop.BarkeeperScala.{DrinkPrepared, PrepareDrink}
import com.typesafe.reactive.workshop.WaiterScala.{DrinkServed, ServeDrink}

class WaiterScala() extends Actor with ActorLogging {
//  val barKeeper = context.actorOf(BarkeeperScala.props(self))
  val barKeeper = context.actorOf(RoundRobinPool(8).props(BarkeeperScala.props(self)),"barkeeper")
  def receive = {
    case ServeDrink(drink:Drink) => barKeeper ! new PrepareDrink(drink,sender())
    case DrinkPrepared(drink, guest) => guest ! DrinkServed(drink)
  }
}



object WaiterScala {
  def props() = Props(new WaiterScala())

  case class DrinkServed(d:Drink)
  case class ServeDrink(d:Drink)

}
