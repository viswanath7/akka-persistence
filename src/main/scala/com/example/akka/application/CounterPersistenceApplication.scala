package com.example.akka.application

import akka.actor.ActorSystem
import com.example.akka.actor.Counter
import com.example.akka.actor.Counter.{Command, Decrement, Increment}
import org.slf4j.LoggerFactory

/**
  * Created by viswanathj on 02/01/2017.
  */
object CounterPersistenceApplication extends App {

  val logger = LoggerFactory getLogger CounterPersistenceApplication.getClass

  logger info "Creating actor system ..."
  val system = ActorSystem("akka-persistent-actors")

  logger info "Creating counter persistent actor ..."
  val counter = system.actorOf(Counter.props)

  logger info "Sending commands to counter actor ..."
  for(index <- 1 until 5)
    counter ! Command(Increment(index))

  Thread.sleep(1000)
  logger info "Shutting down actor system ..."
  system.terminate()

}