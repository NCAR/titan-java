///////////////////////////////////////////////////////////////////////
//
// MessageQueue
//
// Synchronized queue for messages
//
// Mike Dixon
//
// May 2013
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RapidDowControl;

import java.util.*;

public class MessageQueue

{
    
  private LinkedList _list;
  private int _maxSize = 0;
    
  public MessageQueue() {
    _list = new LinkedList();
  }
    
  public void setMaxSize(int maxSize) {
    _maxSize = maxSize;
  }

  public int getMaxSize() {
    return _maxSize;
  }

  public int getSize() {
    return _list.size();
  }

  public boolean isEmpty() {
    if (_list.size() > 0) {
      return false;
    } else {
      return true;
    }
  }

  public void push(Object o) {
    synchronized(_list) {
      if (_maxSize > 0 && _list.size() >= _maxSize) {
        while (_list.size() >= _maxSize) {
          _list.removeLast();
        }
      }
      _list.addFirst(o);
    }
  }

  public Object pop() {
    synchronized(_list) {
      if (_list.size() > 0) {
        return _list.removeLast();
      } else {
        return null;
      }
    }
  }

  public Object get(int index) {
    synchronized(_list) {
      if (index < _list.size()) {
        return _list.get(index);
      } else {
        return null;
      }
    }
  }

}
