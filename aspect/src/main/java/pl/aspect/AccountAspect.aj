package pl.aspect;
import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.FieldSignature;
public aspect AccountAspect {
    pointcut all() :  set(* pl.aspect..*);


    Object around() : all(){
		Field f = ((FieldSignature) thisJoinPoint.getSignature()).getField();
		try {
			f.setAccessible(true);
			System.out.println(f.get(thisJoinPoint.getTarget())+" arg "+thisJoinPoint.getArgs()[0]);
			return proceed();
		} catch(Exception e){
			throw new RuntimeException(e);
		 }finally {
		 	try {
				System.out.println(f.get(thisJoinPoint.getTarget()));
				Integer i = (Integer) f.get(thisJoinPoint.getTarget());
				if(i>500){
				throw new RuntimeException("no");
				}
			} catch(Exception e){
				throw new RuntimeException(e);
		 	}
		}
    }

}