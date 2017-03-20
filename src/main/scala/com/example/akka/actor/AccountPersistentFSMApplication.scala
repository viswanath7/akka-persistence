package com.example.akka.actor

import akka.actor.{ActorLogging, ActorSystem, Props}

import scala.reflect._
import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import com.example.akka.actor.Account._
import org.slf4j.LoggerFactory

import scala.reflect.ClassTag

object Account {

  sealed trait AccountSate extends FSMState
  case object Inactive extends AccountSate { override def identifier: String = "inactive" }
  case object Active extends AccountSate { override def identifier: String = "active" }

  sealed trait AccountData { val amount: Double }
  case object Empty extends AccountData { override val amount = 0 }
  case class Balance(override val amount:Double) extends AccountData

  sealed trait AccountEvent
  case class Accepted(amount: Double, transactionType: TransactionType) extends AccountEvent
  case class Rejected(amount: Double, transactionType: TransactionType, reason:String) extends AccountEvent

  sealed trait TransactionType
  case object Credit extends TransactionType
  case object Debit extends TransactionType

  // FSM command
  case class Operation(amount: Double, transactionType: TransactionType)
}

class AccountPersistentFSMApplication extends PersistentFSM[AccountSate,AccountData,AccountEvent] with ActorLogging {

  val logger = LoggerFactory getLogger "AccountPersistentFSM"

  override def persistenceId: String = "accountPersistentFSM"

  override def domainEventClassTag: ClassTag[AccountEvent] = classTag[AccountEvent]

  override def applyEvent(domainEvent: AccountEvent, currentBalance: AccountData): AccountData = {
    domainEvent match {
      case Accepted(amount, Credit) =>
        val newBalance = currentBalance.amount + amount
        logger debug s"New balance: $newBalance"
        Balance(newBalance)
      case Accepted(amount, Debit) =>
        val newBalance = currentBalance.amount - amount
        logger debug s"New balance: $newBalance"
        if(newBalance>0) Balance(newBalance) else Empty
      case Rejected(amount, transactionType, reason) =>
        logger debug s"Retaining old balance $currentBalance"
        currentBalance
    }
  }

  // Starting with inactive state and empty data
  startWith(Inactive, Empty)

  when(Inactive) {
    case Event(Operation(amount, Credit), stateData) =>
      logger debug s"Inactive state: Received a 'Credit' command for amount $amount. State data: $stateData"
      logger debug "Making a transition to 'Active' state."
      goto(Active) applying Accepted(amount, Credit)
    case Event(Operation(amount, Debit), stateData) =>
      logger debug s"Inactive state: Cannot process a 'Debit' command for amount $amount when balance is zero!"
      stay applying Rejected(amount, Debit, "Overdraft is not supported!")
  }

  when(Active) {
    case Event(Operation(amount, Credit), stateData) =>
      logger debug s"Active state: Received 'Credit' command with amount $amount. State data: $stateData"
      stay() applying Accepted(amount, Credit)
    case Event(Operation(amount, Debit), balance) =>
      logger debug s"Active state: received 'Debit' command with amount $amount. State data: $balance"
      if(balance.amount-amount>0) {
        logger debug s"Active state: Accepted 'Debit' for amount $amount."
        stay() applying Accepted(amount, Debit)
      } else if(balance.amount-amount==0) {
        logger debug s"Active state: Accepted 'Debit' for amount $amount. Making a state transition to 'Inactive' state."
        goto(Inactive) applying Accepted(amount, Debit)
      } else {
        logger debug s"Active state: Rejected 'Debit' for amount $amount due to insufficient funds"
        stay() applying Rejected(amount, Debit, "Overdraft is not supported")
      }
  }

}


