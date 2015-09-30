package com.alliancefoundry.dao;

import java.util.ArrayList;
import java.util.List;

import com.alliancefoundry.model.Event;

public class Node {
	private Event event;
	private List<Node> children;
	private boolean inserted;
	
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
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * @param	event to be contained in the child node
	 * @param	top node of the tree being inserted in to
	 * @return	true if the child node was added, false if otherwise
	 */
	protected void insertNode(Event event, Node top){
		top.inserted = false;
		if(event.getParentId() != null && event.getParentId().equals(this.event.getEventId())){
			if(this.children == null) children = new ArrayList<Node>();
			children.add(new Node(event));
			top.inserted = true;
			return;
		} else if(this.children != null){
			for(Node n : this.children){
				n.insertNode(event, top);
			}
		}
	}
}
