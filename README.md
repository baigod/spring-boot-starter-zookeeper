# spring-boot-starter-zookeeper
Spring boot quickly integrates zookeeper with some common tools




```java
@SpringBootApplication
@EnableZookeeper
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}

```


#application.properties configuration.

```yaml
zookeeper.servers=192.168.1.1:2181

```



The parent of this project can be modified by itself. I use the upper POM.xml.
```java
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.5.6.RELEASE</version>
</parent>
```




Donation developer (ETH)<br>
0x23b96A20Fae711ED6D286feAEED437a6831e3dD7