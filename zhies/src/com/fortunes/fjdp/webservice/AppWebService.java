package com.fortunes.fjdp.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.stereotype.Service;

@WebService
@Service
public class AppWebService {
	
	@WebMethod
	public String hello(String world){
		return "hello, "+world;
	}
	
	
	
}


