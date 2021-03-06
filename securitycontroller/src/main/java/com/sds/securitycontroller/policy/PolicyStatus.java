package com.sds.securitycontroller.policy;
/**
 * Policy applied status
 * @author wxt
 *
 */
public enum PolicyStatus{
	GENERATING,		//Generating 
	GENERATED,		//Generated by PolicyResolver
	APPLIED,		//Command generated and applied by NC
	FAILED,			//Command not applied within specific time
	BACKTRACKED,	//Command applying status has been informed 
	EXPIRED,			//Expired
	UNDEFINED
}