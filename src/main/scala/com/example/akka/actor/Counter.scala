package com.example.akka.actor

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence._
import com.example.akka.actor.Counter._
import org.slf4j.LoggerFactory

/**
  * Created by viswanathj on 02/01/2017.
  */
object Counter {

  val props: Props = Props[Counter]

  sealed trait Operation {
    val count:Int
  }
  case class Increment(override val count:Int) extends Operation
  case class Decrement(override val count:Int) extends Operation

  case class Command(operation: Operation)
  case class Event(operation: Operation)

  case class State(count:Int)
}

class Counter extends PersistentActor with ActorLogging {

  val logger = LoggerFactory getLogger Counter.getClass

  // Initial state of the counter actor
  var state: State = State(count = 0)

  def updateState(event: Event): Unit = event match {
    case Event(Increment(count)) => logger info s"Using increment event to update state with $count"
      state = State(count = state.count + count)
      if(state.count % 10 == 0) {
        saveSnapshot(state)
      }
      logger info s"Current state of the counter is: $state"
    case Event(Decrement(count)) =>
      logger info s"Using decrement event to update state with $count"
      state = State(count = state.count - count)
      logger info s"Current state of the counter is: $state"
      if(state.count % 10 == 0) {
        saveSnapshot(state)
      }
  }

  // Handler to recover the state of actor from journal
  override def receiveRecover: Receive = {
    case event:Event =>
      logger info s"Recovery mode: Processing event -> $event"
      updateState(event)
    case SnapshotOffer(metadata, snapshotedState:State) =>
      logger info s"Recovery mode: Processing snapshot state -> $snapshotedState"
      state = snapshotedState
    case RecoveryCompleted =>
      logger info "Recovery of actor's state from journal was completed!"
  }

  // Command handler
  override def receiveCommand: Receive = {
    case Command(operation) =>
      logger info s"Received command with operation: $operation"
      val event = Event(operation)
      persist(event) { successfullyPersistedEvent => updateState(event) }
    case SaveSnapshotSuccess(snapshotMetadata) =>
      logger info s"Snapshot saved successfully!  $snapshotMetadata"
    case SaveSnapshotFailure(snapshotMetadata, throwable)=>
      logger warn s"Error encountered while saving snapshot. Metadata: $snapshotMetadata \t Reason: $throwable"
  }

  // Unique identifier for persistence actor that has shall be used by messages in journal
  override def persistenceId: String = "counter-persistent-actor"
}