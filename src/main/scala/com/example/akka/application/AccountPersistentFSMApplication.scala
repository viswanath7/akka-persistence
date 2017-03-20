package com.example.akka.application

import akka.actor.{ActorSystem, Props}
import com.example.akka.actor.Account.{Credit, Debit, Operation}
import com.example.akka.actor.AccountPersistentFSMApplication
import org.slf4j.LoggerFactory


object AccountPersistentFSMApplication extends App {

  val logger = LoggerFactory getLogger AccountPersistentFSMApplication.getClass

  logger info "Creating an actor system"
  val actorSystem = ActorSystem("persistent-fsm-actors")


  val accountPersistentFSMActor = actorSystem.actorOf(Props[AccountPersistentFSMApplication])

  accountPersistentFSMActor ! Operation(1000, Credit)
  accountPersistentFSMActor ! Operation(500, Debit)
  accountPersistentFSMActor ! Operation(500, Debit)

  Thread.sleep(1000)

  logger info "Terminating the actor system"
  actorSystem.terminate()
}
