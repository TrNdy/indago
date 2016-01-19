package com.indago.brandnewfg;

public interface AssignmentMapper< A, B >
{
	public Assignment< B > map( Assignment< A > assignment );
}