package com.alliancefoundry.dao;

import java.util.ArrayList;
import java.util.List;

import com.alliancefoundry.model.Event;

public class Node {
	private Event event;
	private List<Node> children;
	
	/**
	 * @param event	stored in the node
	 */
	public Node(Event event){
		this.event = event;
	}
	
	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @return the children
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * @param	event to be contained in the child node
	 * @return	true if the child node was added, false if otherwise
	 */
	protected boolean insertNode(Event event){
		if(event.getParentId() != null && event.getParentId().equals(this.event.getEventId())){
			if(this.children == null) children = new ArrayList<Node>();
			children.add(new Node(event));
			return true;
		} else {
			if(this.children != null){
				for(Node n : this.children){
					return n.insertNode(event);
				}
			}
		}
		return false;
	}
}
