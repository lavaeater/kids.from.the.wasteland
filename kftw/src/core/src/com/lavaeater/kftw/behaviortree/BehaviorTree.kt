package com.lavaeater.kftw.behaviortree

enum class NodeStatus {
  FAILURE,
  SUCCESS,
  RUNNING
}

interface INode {
  fun run(): NodeStatus
}

abstract class Node<T>(val blackBoard:T) : INode

abstract class LeafNode<T>(blackBoard: T):Node<T>(blackBoard)

abstract class CompositeNode<T>(blackBoard: T, val children: List<INode>) : Node<T>(blackBoard)

abstract class DecoratorNode<T>(blackBoard: T, val child: INode):Node<T>(blackBoard)

class Sequence<T>(blackBoard: T, children: List<INode>) : CompositeNode<T>(blackBoard, children) {

  var currentIndex = 0;

  override fun run(): NodeStatus {
    while ()
  }

  fun executeNextChild() : NodeStatus {
    
  }

}