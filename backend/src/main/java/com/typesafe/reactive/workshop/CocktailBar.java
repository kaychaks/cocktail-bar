package com.typesafe.reactive.workshop;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.io.Serializable;

public class CocktailBar extends AbstractLoggingActor {

  private ActorRef waiter = createWaiter();

  private ActorRef createWaiter() {
    return getContext().actorOf(WaiterScala.props(), "waiter");
  }

  public static Props props() {
    return Props.create(CocktailBar.class, () -> new CocktailBar());
  }


  public static final class CreateGuest implements Serializable {
    public final Drink drink;
    private final int maxDrinkCount;

    public CreateGuest(Drink drink, int maxDrinkCount) {
      this.drink = drink;
      this.maxDrinkCount = maxDrinkCount;
    }

  }

  public CocktailBar() {
    log().info("Cocktail bar is open!");
  }

  @Override
  public PartialFunction<Object, BoxedUnit> receive() {
    return ReceiveBuilder
            .match(CreateGuest.class, this::createGuest)
            .build();
  }

  private ActorRef createGuest(CreateGuest createGuest) {
    return getContext().actorOf(Guest.props(waiter, createGuest.drink, createGuest.maxDrinkCount));
  }

  @Override
  public SupervisorStrategy supervisorStrategy() {
    return new OneForOneStrategy(false, DeciderBuilder.
            match(GuestScala.DrunkException.class,e -> SupervisorStrategy.restart()).build());
  }




}
