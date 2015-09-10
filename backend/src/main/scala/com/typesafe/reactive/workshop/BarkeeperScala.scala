package com.typesafe.reactive.workshop

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.typesafe.reactive.workshop.BarkeeperScala.{DrinkPrepared, PrepareDrink}

import scala.concurrent.duration.FiniteDuration

/**
 * Created by kaushik-139137 on 10/09/15.
 */
class BarkeeperScala(guest:ActorRef) extends Actor with ActorLogging{
  val prepareDrinkDuration = FiniteDuration(2L,TimeUnit.SECONDS)

  def receive() = {
    case PrepareDrink(d,a) => sender() ! prepareDrink(prepareDrinkDuration,d,a)
  }

  def prepareDrink(f:FiniteDuration,d:Drink,g:ActorRef) = {
    Utils.busy(f)
    new DrinkPrepared(d,g)
  }
}

object BarkeeperScala {
  def props(a:ActorRef) = Props(new BarkeeperScala(a))
  case class PrepareDrink(d:Drink, a: ActorRef)
  case class DrinkPrepared(d:Drink,forGuest:ActorRef)

}
