# PROYECTO BD_SCOTT + SPRINGBOOTWEB

Proyecto de practica para aprender y consolidar los conocimientos con SpringBoot para el desarrollo web, utilizando la conocida bbdd bd_scott.

---

## Parte 1: Creando la plantilla del proyecto

* La base el proyecto se ha hecho con ![spring.initializr](https://start.spring.io/)

* Las dependencias instaladas son:

  * `Spring Data JPA`: Facilita la interaccion con la base de datos, mediante la implementacion de `JpaRepository` y las `Query Methods`. Tambien facilita las operaciones de Pagination y Sorting sin escribir sql. Todo mediante `Hibernate` que es quien mueve loshilos detas de `JPA`.

  * `Spring Web`: Escencial para crear aplicacioes web, APIRest o MVC tradicionales. Provee anotaciones esenciales como `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.

  * `Spring Boot DevTools`: Herramienta esencial para desarrollar. Tiene Automatic Restart cuando detecta cambios en el `classpath`, refresca automaticamente la página cuando detecta cambios `HTML` o `CSS`.

  * `MySQL Driver`: Conector especifico para `MySQL` con `JDBC Driver`, implementa el estándar `Java Database Connectivity`. Traduce las llamadas de `Java` y `JPA` a sentencias `SQL`.

  * `Thymeleaf`: Se utiliza para el `Server-Side Rendering` (`SSR`). A diferencia de frameworks como `React` o `Angular` que renderizan en el navegador, `Thymeleaf` genera el `HTML` en el servidor antes de enviarlo al cliente.

  * `Validation`: Provee un mecanismo declarativo para asegurar la Integridad de los Datos que entran a la aplicación. Permite usar anotaciones en las clases `model` como `@NotNull`, `@Size`, `@Min`, `@Max`, etc. Su integracion a los `Controllers` es mediante el uso de la anotación `@Valid`. Esta dependencia evitar llenar la lógica con bloques `if-else`.

---

## Parte 2: Creando el Modelo

Para crear un modelo, hay que tener en claro cuales son los campos de la tabla que vamos a modelar, despues de todo un modelo es una tabla en una bbdd. por ejemplo la tabla emp en la bbdd es la siguiente:

### Analizando la tabla

```sql
CREATE TABLE `emp` (
  `EMPNO` int NOT NULL,
  `ENAME` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish2_ci DEFAULT NULL,
  `JOB` varchar(9) CHARACTER SET utf8mb3 COLLATE utf8mb3_spanish2_ci DEFAULT NULL,
  `MGR` int DEFAULT NULL,
  `SAL` float DEFAULT NULL,
  `COMM` float DEFAULT NULL,
  `DEPTNO` int DEFAULT NULL,
  `HIREDATE` date DEFAULT NULL,
  PRIMARY KEY (`EMPNO`),
  KEY `empleado_ibfk_1` (`DEPTNO`),
  CONSTRAINT `emp_ibfk_1`
    FOREIGN KEY (`DEPTNO`)
    REFERENCES `dept` (`DEPTNO`)
    ON DELETE CASCADE ON UPDATE CASCADE
)
```

Al ver la tabla podemos observar detalles que luego nos seran utiles para modelar la tabla.

1. El unico campo `NOT NULL` es `EMPNO`.
2. La `PK` es el campo `EMPNO` y no tiene `AUTO INCREMENT`
3. Tiene una auto referencia entre `EMPNO` y `MGR`
4. Tiene una `FK` a la tabla `DEPT` con el campo `DEPTNO`
5. `ENAME` tiene un limite de 45 caracteres y `JOB` tiene un limite de 9 caracteres.

Con esa información ya se va armando el escenario para la creacion del modelo.

### Armando el modelo

```java
@Entity
@Table(name = "emp")
public class Emp {
  @Id
  @Column(name = "empno", nullable = false)
  private int empno;

  @Column(name = "ename", length = 45)
  private String ename;

  @Column(name = "job", length = 9)
  private String job;

  @ManyToOne
  @JoinColumn(name = "mgr", referencedColumnName = "empno")
  private Emp manager;

  @Column(name = "sal")
  private float sal;

  @Column(name = "comm")
  private float comm;

  @ManyToOne
  @JoinColumn(name = "deptno", referencedColumnName = "deptno")
  private Dept dept;

  @Column(name = "HIREDATE")
  private LocalDate hiredate;

  public Emp() {
  }
  
  // Setters, Getter, Equals, HashCode y ToString
}
```

#### Detalle de las anotaciones

* `@Entity`: Marca la clase como una entidad. Esto indica al Persistence Context que esta clase debe ser mapeada a una tabla en la base de datos.
  * Parametros: si no tiene usa por defecto.
    * `name`: Permite definir un nombre logico para la entidad en las consultas JPQL. default `Emp`

* `@Table(...)`: Especifica el nombre de la tabla en la base de datos.
  * Parametros:
    * `name`: Nombre de la tabla. Si se omite `JPA` usara el nombre de la clase.
    * `schema`: Define el esquema de base de datos (ej: `public`, `sales`).
    * `indexes`: Permite definir indices secundarios (`@Index`) para optimizacion de consultas.

* `@Id`: Marca el campo como la clave primaria de la entidad. Normalmente se combina con:
  * `@GeneratedValue` para autoquenerar valores, pero como esta tabla no usa autoincrement, el `empno` se ingresa manual o alternativas.
    * Parametros:
      * `strategy = GenerationType.IDENTITY`: Autoinclemento de MySQL.
      * `strategy = GenerationType.SEQUENCE`: Para Oracle/PostgreSQL.
      * `strategy = GenerationType.UUID`: Estándar desde JPA 3.1.

* `@Column(...)`: Mapea el atributo de la clase a una columna de la tabla.
  * Parametros:
    * `name`: Nombre de la columna en la base de datos (ej: `empno`, `ename`).
    * `nullable = false`: Añade una restricción `NOT NULL` a nivel de DDL y validación en runtime. Si no se agrega el default es `true`.
    * `length`: Define la longitud para tipo `VARCHAR` o `CHAR`.
    * `unique`: `true/false`. Crea una restricción `UNIQUE` en la columna.
    * `updatable` / `insertable`: Si es `false, la columna se ignora en sentencias `UPDATE` o `INSERT`.
    * `precision` / `scale`: Para números decimales (ej: `BIGDECIAML`).
    * `commnet`: Añade un comentario al DLL de la columna.
    * `check`: Permite definir una restricción `CHECK` en la columna.

* `@ManyToOne`: Define una relación de cardinalidad "Muchos a Uno"
  * Muchos `Emp` tienen un manager (otro `Emp`)
  * Muchos `Emp` pertenecen a un `Dept`
  * Parametros:
    * `fetch`: controla la estrategia de carga.
      * `FetchType.LAZY`: Carga diferiad (Recomendado para performance).
      * `FetchType.EAGER`: Carga inmediata (Perfecto para `@ManyToOne`).
    * `cascade`: Propagar operaciones.
      * `CascadeType.PERSIST`: Si persisto una entidad padre, sus hijos tambien se persisten.
      * `CascadeType.MERGE`: Si actualizo una entidad padre, sus hijos tambien se actualizan.
      * `CascadeType.REMOVE`: Si borro una entidad padre, sus hijos tambien se borran.
      * `CascadeType.REFRESH`: Si refresco una entidad padre, sus hijos tambien se refrescan.
      * `CascadeType.DETACH`: Si detacho una entidad padre, sus hijos tambien se detachan.
      * `CascadeType.ALL`: Propagar todas las operaciones.
      * TIP: No confundir `.REMOVE` con `orhpanRemoval=true`. La primera solo borra al hijo si remuevo el padre explicitamente, mientras que la segunda Borra al hijo si simplemente se rompe la relación con el padre.

* `@JoinColumn(...)`: Especifica la comlumna que actúa como `FK`.
  * Parametros:
    * `name`: Nombre de la columna.
    * `referencedColumnName`: Nombre de la columna en la tabla relacionada (`Emp.empno` o `Dept.deptno`).

* `@OneToMany`: La contramarte de `@ManyToOne`. Se usa en la Clase `Dept` para tener una lista de empleados `List<Emp>`
  * Parametros:
    * `mappedBy`: Indica quien es el dueño de la relacion.

* `@Lob`: `Large Object`, esencial cuando se necesita almacenar grandes volúmenes de datos en un solo campo. Indica que la propiedad debe persistirse como un `Large Object` en la bbdd.
  * Variantes: Segun el tipo de datp para la columna.
    * `CLOB`: Character Large Object: Si es de tipo `String` o `char[]`.
      * Se usa para textos muy largos (ej: contenido de un articulo, un XML, un JSON, etc...).
    * `BLOB`: Binary Large Object: Si es de tipo `byte[]`.
      * Se usa para archivos binarios (ej: imagenes, videos, sonidos, pdf's, etc...).
  * Se combina con:
    * `@Basic(fetch = FetchType.LAZY)`: Crucial. Por defecto los LOBs se cargan inmediatamente. Por rendimiento, casi siempre es mejor cargarlos bajo demanda (LAZY LOADING), para no traer 50MB de datos si solo necesitabas leer el ID o el nombre del usuario.
    * `@Column(length = 1000)`: En algunas bbdd ayuda a definir el tamaño máximo esperado.

* `@Enumerated`: Sirve para mapear Java Enums. Define como se guarda un Enum en la BBDD.
  * Parametros:
    * `EnumType.ORDINAL`: Por defecto. Guarda el indice numerico del enum (0, 1, 2, 3, etc...). No recomendado, si cambias el orden de los enums en el código, se rompen los datos antiguos.
    * `EnumType.STRING`: Recomendado. Guarda el nombre del enum como texto ("ACTIVO", "PENDIENDTE", "INACTIVO", etc...).

* `@Transient`: Indica a JPA que ignore este campo. No se creará una columna en la tabla y su valor no se guardará ni se recuperará de la base de datos. Se usa en campos calculados (ej. edad calculado a partir de fechaNacimiento), contraseñas en texto plano durante el registro (antes de ser hasheadas), o estados temporales de la UI.

* `@Embedded` y `@Embeddable`: Estas anotaciones permiten aplicar principios de Domain-Driven Design (DDD) agrupando columnas relacionadas.
  * `@Embeddable`: Se coloca en una clase POJO simple (no es una @Entity) para indicar que sus campos pueden ser integrados en otra tabla.
  * `@Embedded`: Se usa en la Entidad principal para inyectar la clase @Embeddable.
  * Resultado en DB: No crea una tabla extra. Las columnas de la clase embebida se "aplanan" (flatten) dentro de la tabla de la entidad padre.

* `@ElementCollection`: Se usa para una relación One-to-Many simple donde los hijos no son Entidades completas, sino tipos básicos (Strings, Integers) o clases `@Embeddable`. Una lista de teléfonos, etiquetas (tags) o roles simples.
  * Diferencia con `@OneToMany`: El ciclo de vida depende totalmente del padre. Si borras al padre, desaparecen los elementos de la colección automáticamente sin necesidad de configurar cascadas complejas.

* `@Version`: Optimistic Locking. Agrega control de concurrencia optimista. Cada vez que actualizas la entidad, JPA incrementa este número automáticamente. Si dos usuarios intentan guardar la misma entidad al mismo tiempo, el segundo fallará con una `OptimisticLockException` porque el número de versión ya no coincidirá.

---

## Parte 3: Creando el Repositorio

Ya con el modelo creado podemos crear el repositorio.

### Creando el repositorio `@Repository`

Spring Data JPA utiliza una funcionalidad llamada `Query Methods` (o `Derived Query Methods`). Básicamente, interpreta el nombre del método en inglés y genera la SQL Query automáticamente en tiempo de ejecución.

```java
public interface EmpRepository extends JpaRepository<Emp, Integer>{
    List<Emp> findByDept(Dept dept);
    boolean existsByEmpno(Integer empNo);
    Page<Emp> findByJobIgnoreCaseContaining(String job, Pageable pageable);
    Page<Emp> findByEnameIgnoreCaseContaining(String ename, Pageable pageable);
    Page<Emp> findBySalGreaterThanEqual(Float sal, Pageable pageable);
    Page<Emp> findByCommGreaterThanEqual(Float comm, Pageable pageable);
    Page<Emp> findByDept_Deptno(Integer deptno, Pageable pageable);
}
```

#### Explicacion de los `Query Methods` implementados

* `List<Emp> findByDept(Dept dept);`:
  * Busca empleados por Exact Match de la entidad Dept.
  * SQL Generado: Algo como `SELECT * FROM emp WHERE dept_id = ?`.
  * Detalle: Spring es lo suficientemente inteligente para tomar la Primary Key del objeto Dept que le pases y usarla en la cláusula WHERE.

* `boolean existsByEmpno(Integer empNo);`:
  * Verifica si un registro existe sin traer toda la data.
  * SQL Generado: Optimizado, usualmente `SELECT 1 FROM emp WHERE empno = ? LIMIT 1`.
  * Ventaja: Es mucho más eficiente (High Performance) que hacer un `findBy...` y verificar si la lista está vacía, ya que ahorra ancho de banda y memoria.

* `Page<Emp> findByJobIgnoreCaseContaining(String job, Pageable pageable);`:
  * Búsqueda de texto flexible.
  * Keywords:
    * `IgnoreCase`: Ignora mayúsculas/minúsculas (hace el match "insensitive").
    * `Containing`: Agrega comodines (wildcards) automáticamente (`%valor%`).
  * SQL Generado: `... WHERE UPPER(job) LIKE UPPER(%?%) ....`
  * Pagination: El objeto Pageable agrega automáticamente `LIMIT` y `OFFSET` al `SQL` para paginar los resultados.

* `Page<Emp> findByEnameIgnoreCaseContaining(...);`:
  * Idéntico al anterior, pero aplicando el filtro sobre la columna ename. Útil para barras de búsqueda (Search Bars).

* `Page<Emp> findBySalGreaterThanEqual(Float sal, Pageable pageable);`
  * Filtrado por rango numérico.
  * Keyword:
    * `GreaterThanEqual` se traduce al operador matemático `>=`.
  * SQL Generado: `... WHERE sal >= ? ...`
  * Uso: "Dame todos los empleados que ganen X o más".

* `Page<Emp> findByCommGreaterThanEqual(Float comm, Pageable pageable);`
  * Igual al anterior, aplicado a la comisión (comm).

* `Page<Emp> findByDept_Deptno(Integer deptno, Pageable pageable);`:
  * Property Traversal (Navegación de propiedades).
  * Aquí no pasas el objeto `Dept` entero, sino solo su `ID` (`Integer`).
  * SQL Generado: `... WHERE dept_id = ? ...`
  * Sintaxis `_`: El guion bajo (`_`) es explícito. Le dice a Spring: "Ve a la propiedad dept de Emp, entra en ella y busca su propiedad deptno".
  * Uso: Muy útil cuando recibes solo el ID desde el Frontend y no quieres instanciar un objeto `Dept` completo para hacer la consulta.

### Otras alternativas

Aunque los Derived Query Methods (los de arriba) cubren el 80% de los casos, en proyectos complejos necesitarás más control.

#### `@Query` (`JPQL` y `Native Query`)

Cuando el nombre del método se vuelve ridículamente largo (ej. `findByNameAndAgeLessThanAndActiveTrue...`), o la consulta es muy compleja, usas `@Query`.

1. `JPQL` (`Java Persistence Query Language`): Consultas orientadas a objetos.

    ```java
    // Consultas sobre la ENTIDAD (Emp), no sobre la tabla
    @Query("SELECT e FROM Emp e WHERE e.sal > :minSal AND e.job = 'MANAGER'")
    List<Emp> findRichManagers(@Param("minSal") Float minSal);
    ```

2. `Native Query`: `SQL` puro y duro (dependiente de la base de datos).

    ```java
    @Query(value = "SELECT * FROM emp WHERE ename REGEXP '^[A-M]'", nativeQuery = true)
    List<Emp> findEmployeesStartWithAtoM();
    ```

#### `@Modifying` + `@Transactional`

Por defecto, los repositorios son solo para lectura. Si quieres hacer un `UPDATE` o `DELETE` personalizado con `@Query`, necesitas estas anotaciones extra.

```java
@Modifying
@Transactional // Requerido para operaciones de escritura
@Query("UPDATE Emp e SET e.sal = e.sal * 1.10 WHERE e.dept.deptno = :deptno")
void giveRaiseToDepartment(@Param("deptno") Integer deptno);
```

#### `@Param`

Se usa para nombrar los parámetros en la query y evitar confusiones si cambias el orden de los argumentos en el método Java. Es una Best Practice para mantener el código legible.

#### Projections (Interfaces de proyección)

A veces no quieres traer el objeto `Emp` completo (que puede ser pesado con muchas relaciones). Puedes definir una interfaz pequeña solo con los datos que necesitas. Spring implementará esto al vuelo.

```java
// Interfaz ligera
public interface EmpNameAndSal {
    String getEname();
    Float getSal();
}

// En el repositorio
List<EmpNameAndSal> findByJob(String job);
```

Esto hace el `SQL SELECT ename, sal FROM...` en lugar de `SELECT *`, optimizando el rendimiento.

#### JpaSpecificationExecutor (Criterios Dinámicos)

Si necesitas filtros dinámicos (ej. un usuario filtra por nombre, otro por fecha, otro por ambos), crear métodos `findBy` para cada combinación es imposible. Para eso, tu repositorio extiende `JpaSpecificationExecutor<Emp>`. Esto te permite construir queries programáticamente (tipo "LEGO") usando la Criteria API.


#### `@QueryHints`

Los `@QueryHints` son, metafóricamente, "susurros al oído" que le das a Hibernate para decirle cómo ejecutar una consulta de manera más eficiente.

A diferencia de `@Query` (que define qué datos traer), `@QueryHints` define cómo traerlos. No cambian el resultado de la búsqueda, pero sí el rendimiento y el consumo de recursos.

Son instrucciones específicas para el proveedor de persistencia. se usan para:

* Performance Tuning (Optimizar el rendimiento)
* Gestionar la memoria y evitar `OutOfMemoryError`
* Timeouts (Control de tiempos)

##### `Hints`

| Hint | Descripción | Cuando usar |
| :--- | :---------- | :---------- |
| `HINT_READ_ONLY` | "Solo voy a leer, no voy a editar" | En reportes, listados, dashboards y cualquier `GET` masivo |
| `HINT_FETCH_SIZE` | "Trae los datos por paquetes, no todos juntos" | Cuando esperas listas muy grandes (> 1000 registros) pero necesitas procesarlos todos |
| `HINT_CACHEABLE` | "Guarda este resultado en caché (L2 Cache)" | Para datos que casi nunca cambian (ej. lista de países, categorías) |
| `jakarta.persistence.query.timeout` | "Si tardas mucho, cancélalo" | Consultas pesadas que podrían bloquear la base de datos |
| `HINT_COMMENT` | "Ponle una etiqueta a esta query en los logs de la BD" | "Ponle una etiqueta a esta query en los logs de la BD" |

##### Ejemplo en `Emp`

```java
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import static org.hibernate.jpa.HibernateHints.*;

public interface EmpRepository extends JpaRepository<Emp, Integer> {

    // 1. Optimización para listas grandes
    // Trae los empleados de 50 en 50 para no saturar la memoria RAM.
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "50"))
    List<Emp> findByDept(Dept dept);

    // 2. Optimización para reportes y paginación (Solo lectura)
    // Desactiva el "Dirty Checking". Hace la consulta mucho más rápida.
    @QueryHints(@QueryHint(name = HINT_READ_ONLY, value = "true"))
    Page<Emp> findByJobIgnoreCaseContaining(String job, Pageable pageable);

    // Aplicado a tus otras búsquedas paginadas...
    @QueryHints(@QueryHint(name = HINT_READ_ONLY, value = "true"))
    Page<Emp> findByEnameIgnoreCaseContaining(String ename, Pageable pageable);

    // 3. Timeout (Seguridad)
    // Si la base de datos está lenta y tarda más de 3 segundos, corta la conexión
    // para no dejar al usuario esperando eternamente.
    @QueryHints(@QueryHint(name = "jakarta.persistence.query.timeout", value = "3000"))
    Page<Emp> findBySalGreaterThanEqual(Float sal, Pageable pageable);
}
```

###### Explicación de este código

* `HINT_READONLY`: Estos métodos que devuelven `Page<Emp>` son claramente para mostrar datos en una tabla o lista en el `Frontend`. No vas a modificar esos objetos dentro de esa transacción.
  * El Problema: Por defecto, `Hibernate` guarda una copia de cada objeto que traes en su memoria (`First Level Cache`) para hacer `Dirty Checking` (verificar si algo cambió para hacer un `update`). Esto gasta `memoria` y `CPU` inútilmente si solo estás leyendo.
  * La Solución: `HINT_READONLY` le dice a `Hibernate`: "No vigiles estos objetos, solo entrégamelos".

* `HINT_FETCH_SIZE`: Para el método `findByDept(Dept dept)`, si un departamento tiene 50,000 empleados, `Hibernate` intentará traerlos todos de golpe en una sola petición de red gigantesca o de forma muy ineficiente.
  * La Solución: `HINT_FETCH_SIZE` le dice al `driver JDBC`: "Tráeme los datos en paquetes de 50 en 50".

#### `@Lock`(Control de Concurrencia)

A veces no quieres optimizar lectura, sino asegurar escritura.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Emp> findByEmpno(Integer empNo);
```

Esto hace un `SELECT ... FOR UPDATE`, bloqueando el registro en la BBDD para que nadie más lo toque hasta que termines.

#### `@EntityGraph` (El rey del rendimiento en relaciones)

Soluciona el famoso problema `N+1 Selects`. Si al traer `Emp` también quieres traer su `Dept` en la misma query (`JOIN`) en lugar de hacer queries separadas.

```java
// Hace un LEFT JOIN con 'dept' automáticamente
@EntityGraph(attributePaths = {"dept"})
List<Emp> findByJob(String job);
```

---

## Parte 4: Creando el Servicio Interface `EmpService` y su implementacion `EmpServiceImpl`

En la arquitectura de `Spring Boot`, el `Service Layer` es el corazón de la lógica de negocio (`Business Logic`). Es el intermediario que orquesta el flujo de datos entre el `Controller` (que recibe la petición `HTTP`) y el `Repository` (que habla con la base de datos).

### La interface `EmpService`

```java
public interface EmpService {
    List<Emp> findAll();
    Page<Emp> findAllPage(Pageable pageable);
    void saveEmp(Emp emp);
    Emp saveNewEmp(Emp newEmp);
    Optional<Emp> findById(Integer id);
    List<Emp> findByDept(Dept dept);
    void deleteById(Integer id);
    Page<Emp> findByCriteria(String type, String value, Pageable pageable);
}
```

Define el "contrato" de operaciones disponibles.

* `Decoupling` (Desacoplamiento): Permite que el controlador no dependa de la implementación concreta. Esto facilita el Unit Testing, ya que puedes crear "Mocks" de esta interfaz fácilmente.

### La implementacion `EmpServiceImpl`

```java
@Service
public class EmpServiceImpl implements EmpService{

    private final EmpRepository empRepository;

    public EmpServiceImpl(EmpRepository empRepository){
        this.empRepository = empRepository;
    }

    @Override
    public void deleteById(Integer id) {
        empRepository.deleteById(id);
    }

    @Override
    public List<Emp> findAll() {
        return empRepository.findAll();
    }

    @Override
    public Page<Emp> findAllPage(Pageable pageable) {
        return empRepository.findAll(pageable);
    }

    @Override
    public Optional<Emp> findById(Integer id) {
        return empRepository.findById(id);
    }

    @Override
    public void saveEmp(Emp emp) {
        empRepository.save(emp);
    }

    @Override
    public Emp saveNewEmp(Emp emp) {
        Integer empnoFromUser = emp.getEmpno();
        if(empnoFromUser == null || empnoFromUser <= 0) {
            throw new IllegalArgumentException("El 'EMPNO' no puede ser nullo o <= 0");
        }
        if (empRepository.existsByEmpno(empnoFromUser)) {
            throw new IllegalArgumentException("El 'EMPNO' ya existe");
        }
        return empRepository.save(emp);
    }

    @Override
    public List<Emp> findByDept(Dept dept) {
        return empRepository.findByDept(dept);
    }

    @Override
    public Page<Emp> findByCriteria(String type, String value, Pageable pageable) {
        switch (type) {
            case "ename":
                return empRepository.findByEnameIgnoreCaseContaining(value, pageable);
            case "job":
                return empRepository.findByJobIgnoreCaseContaining(value, pageable);
            case "sal":
                try {
                    return empRepository.findBySalGreaterThanEqual(Float.parseFloat(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            case "comm":
                try {
                    return empRepository.findByCommGreaterThanEqual(Float.parseFloat(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            case "deptno":
                try {
                    return empRepository.findByDept_Deptno(Integer.parseInt(value), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty();
                }
            default:
                break;
        }
        return empRepository.findAll(pageable);
    }
}
```

* `@Service`: Esta anotación registra la clase como un Spring Bean en el contenedor de inyección de dependencias (IoC Container). Por defecto, es un Singleton.

* Constructor `public EmpServiceImpl(EmpRepository empRepository){ ... }`

    Estamos usando la `Constructor-based Dependency Injection`, que es la Best Practice actual (superior a usar `@Autowired` en el campo). Hace que la dependencia sea obligatoria e inmutable, y facilita los tests sin necesidad de levantar todo el contexto de `Spring`.

* Validación de Negocio (`saveNewEmp`): Aquí es donde resalta el servicio. No solo guardas el dato, sino que validas reglas de negocio (ej. "El ID no puede ser nulo", "El empleado no debe existir"). Esto protege la integridad de tu base de datos antes de siquiera intentar insertar.

* Lógica de Criterios (`findByCriteria`): Centralize la lógica de filtrado dinámico.

### Posibles optimizaciones

Para llevar este servicio a un nivel profesional (Production Ready), te sugiero aplicar las siguientes mejoras clave:

#### Gestión de Transacciones (`@Transactional`)

Actualmente, mis métodos corren sin un contexto transaccional explícito (o dependen del que tenga el repositorio). Para optimizar debería controlar esto explícitamente en el servicio.

* `@Transactional` (Clase o Métodos de escritura): Garantiza `Atomicity`. Si falla algo a mitad del método, se hace un Rollback de todo.

* `@Transactional(readOnly = true)`: Para métodos de búsqueda (`findAll`, `findById`).

Beneficio: Mejora la Performance. Spring sabe que no necesita hacer "Dirty Checking" (revisar cambios en las entidades) y puede optimizar el uso de la conexión a la base de datos.

#### Eliminar Boilerplate con Lombok

Puedes eliminar el constructor manual y usar `@RequiredArgsConstructor`. Esto genera un constructor con todos los campos marcados como final.

#### Logging `@Slf4j`

Es vital tener Observability. Necesitas saber qué pasa dentro del servicio cuando algo falla en producción.

#### Custom Exceptions

En lugar de `IllegalArgumentException` (que es muy genérica), crea tus propias excepciones (ej. `BusinessRuleException` o `ResourceNotFoundException`) para manejarlas globalmente luego.

#### Posible Corrección

```java
@Slf4j // 1. Genera un logger automáticamente (log.info, log.error)
@Service
@RequiredArgsConstructor // 2. Genera el constructor para 'empRepository' automáticamente
@Transactional(readOnly = true) // 3. Por defecto, todas las operaciones son de solo lectura (optimización)
public class EmpServiceImpl implements EmpService {

    private final EmpRepository empRepository;

    @Override
    @Transactional // 4. Sobreescribimos para permitir escritura (Write operations)
    public void deleteById(Integer id) {
        log.info("Request to delete Emp with id: {}", id); // Logging estructurado
        if (!empRepository.existsById(id)) {
             throw new RuntimeException("Empleado no encontrado para eliminar"); // Mejor manejo de errores
        }
        empRepository.deleteById(id);
    }

    @Override
    public List<Emp> findAll() {
        log.debug("Request to find all Emps");
        return empRepository.findAll();
    }

    // ... findAllPage, findById (heredan readOnly = true)

    @Override
    @Transactional // Escritura
    public void saveEmp(Emp emp) {
        empRepository.save(emp);
    }

    @Override
    @Transactional // Escritura
    public Emp saveNewEmp(Emp emp) {
        log.info("Request to save new Emp: {}", emp.getEname());
        Integer empnoFromUser = emp.getEmpno();
        // Validaciones...
        if(empnoFromUser == null || empnoFromUser <= 0) {
            throw new IllegalArgumentException("El 'EMPNO' no puede ser nulo o <= 0");
        }
        if (empRepository.existsByEmpno(empnoFromUser)) {
            // Sugerencia: Usar una excepción personalizada
            log.warn("Intento de crear empleado duplicado: {}", empnoFromUser);
            throw new IllegalArgumentException("El 'EMPNO' ya existe");
        }
        return empRepository.save(emp);
    }

    // ... resto de métodos
}
```

---

## Parte 5: El controllador `EmpController`

Esta es la capa de presentación (`Presentation Layer`). Aquí es donde `Spring MVC` brilla, conectando la lógica de negocio (`Service`) con la interfaz de usuario (`Thymeleaf`).

Este código implementa un `MVC` Controller clásico (no confundir con `REST API`). Su función es preparar el `Model` (datos) y retornar el nombre de una `View` (plantilla `HTML`).

### Creando el Controlador

```java
@Controller
@RequestMapping("/emp")
public class EmpController {
    // Inyeccion del EmpService
    private final EmpService empService;
    private final DeptService deptService;


    public EmpController(EmpService empService, DeptService deptService) {
        this.empService = empService;
        this.deptService = deptService;
    }

    @GetMapping("/list")
    public String listEmployees(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "value", required = false) String value,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        Model model) {
        Page<Emp> results;
        if (value == null || value.isEmpty()) {
            results = empService.findAllPage(pageable);
        } else {
            results = empService.findByCriteria(type, value, pageable);
        }
        model.addAttribute("empPage", results);
        model.addAttribute("tipoSeleccionado", type);
        model.addAttribute("valorBuscado", value);
        return "emp/list-emp";
    }

    @GetMapping("/createEmp")
    public String createEmployee(Model model) {
        model.addAttribute("emp", new Emp());
        model.addAttribute("emps", empService.findAll());
        model.addAttribute("depts", deptService.findDistinctBy());
        model.addAttribute("editMode", "false");
        return "emp/form-emp";
    }

    @PostMapping("/saveEmp")
    public String saveEmployee(@Valid
            @ModelAttribute("emp") Emp emp,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("emps", empService.findAll());
            model.addAttribute("depts", deptService.findDistinctBy());
            return "emp/form-emp";
        }
        try {
            empService.saveNewEmp(emp);
            return "redirect:/emp/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("empno", "error.emp", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "emp/form-emp";
        }
    }

    @GetMapping("/edit/{id}")
    public String updateEmp(@PathVariable("id") Integer idEmp, Model model){
        model.addAttribute("emp", empService.findById(idEmp).get());
        model.addAttribute("emps", empService.findAll());
        model.addAttribute("depts", deptService.findDistinctBy());
        model.addAttribute("editMode", "true");
        return "emp/form-emp";
    }

    @PostMapping("/update")
    public String updateEmployee(@Valid @ModelAttribute("emp") Emp emp, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("editMode", "true");
            model.addAttribute("emps", empService.findAll());
            model.addAttribute("depts", deptService.findDistinctBy());
            return "emp/form-emp";
        }
        empService.saveEmp(emp);
        return "redirect:/emp/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmp(@PathVariable("id") Integer idEmp, RedirectAttributes attributes) {
        empService.deleteById(idEmp);
        attributes.addFlashAttribute("msg", "Employee deleted");
        return "redirect:/emp/list";
    }
}
```

### Detalle de las anotaciones

#### Definicion y Enrutamiento

* `@Controller`: Marca la clase como un `Web Component`. A diferencia de `@RestController` (que retorna `JSON/XML`), esta anotación indica que los métodos retornarán, por lo general, un `String` que representa el nombre del archivo `HTML` a renderizar.
  * Funcionamiento: `Spring` usa un `ViewResolver` para buscar ese nombre (ej. `"emp/list-emp"`) en la carpeta de templates.

* `@RequestMapping("/emp")`: Define el prefijo base de la URL para todos los métodos de esta clase.
  * Efecto: Todas las rutas serán `localhost:port/emp/....`

* `@GetMapping` / `@PostMapping`: Mapean las peticiones `HTTP` `GET` (recuperar datos/vistas) y `POST` (enviar datos de formularios) a métodos específicos.
  * Nota: Son atajos de `@RequestMapping(method = RequestMethod.GET)`.

#### Inyección y Manejo de Datos

* `@RequestParam`
  * Uso: `public String listEmployees(@RequestParam(...) String type)`
  * Concepto: Extrae parámetros de la `Query String` de la `URL` (ej. `?type=job&value=developer`).
  * `required = false`: Fundamental aquí. Si el usuario entra a `/emp/list` sin filtros, la aplicación no falla; las variables simplemente llegan como `null`.

* `@PathVariable`
  * Uso: `@GetMapping("/edit/{id}")`
  * Concepto: Extrae valores dinámicos integrados directamente en la ruta (`URI Path`), no después del signo `?`.
  * `RESTful Style`: Es esencial para recursos identificables (ej. editar el empleado ID 5).

* `@ModelAttribute`
  * Uso: `@ModelAttribute("emp") Emp emp`
  * Concepto: Vincula los campos del formulario `HTML` con el objeto `Java` automáticamente.
  * `Bidireccional`:
    * De `Java` a `HTML`: Pone el objeto en el modelo para que la vista lo muestre.
    * De `HTML` a `Java`: Recibe los datos del `POST`, instancia un `Emp` y llena sus campos (`Setters`).

#### Validación y Flujo (Validation & Flow)

* `@Valid`
  * Concepto: Activa la validación `JSR-380` definida en la entidad (ej. `@NotNull`, `@Size`). Se ejecuta antes de entrar al cuerpo del método.

* `BindingResult`
  * Concepto: Es el contenedor de los errores de validación.
  * Regla de Oro: Debe ir inmediatamente después del objeto anotado con `@Valid`. Si ponemos otro argumento en medio (como `Model`), `Spring` lanzará una excepción.
  * Uso: `if (result.hasErrors())` permite detener el proceso y devolver al usuario al formulario para que corrija los datos.

* `RedirectAttributes`
  * Concepto: Se usa para el patrón `Flash Attributes`.
  * Problema que resuelve: Cuando se hace un `redirect:/...`, el `Model` se pierde (porque es una nueva petición `HTTP`). `RedirectAttributes` guarda datos temporalmente en la sesión y los borra apenas se leen. Ideal para mensajes de éxito ("Employee deleted").

#### Paginación (Pagination)

* `@PageableDefault(page = 0, size = 10)`
  * Concepto: Configura el comportamiento por defecto si el frontend no envía parámetros de paginación.
  * Magia: Transforma parámetros de `URL` como `?page=1&size=5&sort=ename` directamente en un objeto `Java Pageable`.

### Anotaciones y Opciones No Implementadas

Para un entorno profesional, deberiamos conocer y considerar estas herramientas adicionales:

#### Verbos `HTTP` Correctos (`@PutMapping`, @DeleteMapping)

En el código uso `@GetMapping` para borrar `(/delete/{id})` y `@PostMapping` para actualizar.

* El Estándar: `REST` dicta usar `DELETE` para borrar y `PUT/PATCH` para actualizar.
* El Problema: Los formularios `HTML5` nativos (`<form>`) solo soportan `GET` y `POST`.
* La Solución: `Spring Boot` permite simular estos verbos usando un campo oculto en el formulario llamado `_method` y "engaña" al `Controller` para que crea que recibió el verbo correcto:

    ```html
    <input type="hidden" name="_method" value="delete"/>
    ```

    Y en el `controller` debiera usar  `@DeleteMapping("/{id}")`. Esto es más semántico y correcto.

* Configuración Requerida: En las versiones modernas, este filtro suele estar deshabilitado por defecto para ahorrar recursos. Pero se puede activar en el `application.properties`:

    ```properties
    # Habilita la simulación de verbos HTTP ocultos
    spring.mvc.hiddenmethod.filter.enabled=true
    ```

* Correccion en el `Controller`

    ```java
    @DeleteMapping("/delete/{id}") // Recibe la petición transformada
    public String deleteEmp(@PathVariable("id") Integer id, RedirectAttributes attr) {
        empService.deleteById(id);
        attr.addFlashAttribute("msg", "Employee deleted successfully");
        return "redirect:/emp/list";
    }
    ```

* Implementación con `Thymeleaf`

    ```html
    <form th:action="@{/emp/delete/{id}(id=${emp.id})}" th:method="delete">
        <button type="submit" class="btn btn-danger">Eliminar</button>
    </form>
    ```

#### Manejo Global de Errores (`@ControllerAdvice`)

Actualmente estoy usando `IllegalArgumentException` con un `try-catch` dentro del método `saveEmployee`. Esto puede llegar a ser un problema ya que, si  20 métodos, tendre 20 `try-catch`.

Solución: Usar una clase anotada con `@ControllerAdvice` y métodos `@ExceptionHandler`. Esto centraliza el manejo de errores.

##### Entendiendo el uso de `@ControllerAdvice`

El patrón AOP (Aspect Oriented Programming) nos permite separar la lógica de manejo de errores de la lógica de negocio ("Separation of Concerns").

###### Implementacion

Crea un paquete `exception` o `advice`.

```java
package com.bd_scott.app_bd_scott.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;

@ControllerAdvice // 1. Intercepta excepciones de TODOS los Controllers
public class GlobalExceptionHandler {

    // Caso 1: Manejo específico de tu lógica de negocio
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBusinessError(IllegalArgumentException ex, Model model) {
        // Inyectamos el error en el modelo para mostrarlo en una vista amigable
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("status", "400 Bad Request");
        return "error/generic-error"; // template: templates/error/generic-error.html
    }

    // Caso 2: Catch-all (Cualquier error no previsto)
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        model.addAttribute("error", "Ocurrió un error inesperado. Contacte a soporte.");
        model.addAttribute("details", ex.getMessage()); // Cuidado: no mostrar stacktrace en prod
        return "error/server-error";
    }
}
```

Con esto `EmpController` quedaria limpio. Ya no necesitia `try-catch` en `saveEmployee`. Si el servicio lanza `IllegalArgumentException`, esta clase la atrapa y actua en consecuencia.

#### `@InitBinder`

Permite configurar cómo se "bajan" los datos del Request al Objeto.

* Caso de uso: Convertir automáticamente strings vacíos "" a null, o formatear fechas personalizadas que vienen de inputs de texto plano.

```java
@InitBinder
public void initBinder(WebDataBinder binder) {
    StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
    binder.registerCustomEditor(String.class, stringTrimmer);
}
```

##### Entendiendo el uso de `@InitBinder`

Este es un punto de seguridad y limpieza de datos (`Data Sanitization`). Un problema clásico en formularios web es que un campo vacío llega como `""` (`String` vacío) en lugar de `null`. Esto puede romper validaciones de base de datos o lógica condicional.

##### Implementacion

```java
@Controller
@RequestMapping("/emp")
public class EmpController {

    // Se ejecuta ANTES de que los datos del form lleguen a los métodos @PostMapping
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // StringTrimmerEditor es una clase utilitaria de Spring
        // true = convertir strings vacíos a null
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        // Registramos el editor para todos los datos de tipo String
        binder.registerCustomEditor(String.class, stringTrimmer);
    }

    // ... resto de métodos normales
}
```

Resultado:

* Input usuario: `" "` (espacios) -> Llega al Controller como: `null`
* Input usuario: `""` (vacío) -> Llega al Controller como: `null`
* Input usuario: `" Scott "` -> Llega al Controller como: `"Scott"`

#### `@SessionAttributes`

Mantiene un objeto en la sesión `HTTP` entre múltiples peticiones.

Útil para formularios largos (`Wizards`) o para evitar hacer consultas repetitivas a la base de datos durante la edición de un objeto.

* Caso de uso: Un formulario `Wizard` de varios pasos:
  * Paso 1: Datos personales
  * Paso 2: Dirección
  * Paso 3: Confirmación
  * El objeto se mantiene vivo hasta que llamas a `status.setComplete()`

##### Implementacion

```java
@Controller
@RequestMapping("/wizard")
@SessionAttributes("empleadoWizard") // 1. Guarda este atributo en la sesión HTTP
public class EmployeeWizardController {

    @GetMapping("/step1")
    public String showStep1(Model model) {
        // Inicializamos el objeto, se guarda en sesión automáticamente
        model.addAttribute("empleadoWizard", new Emp());
        return "wizard/step1"; // Pide Nombre y Apellido
    }

    @PostMapping("/step2")
    public String processStep1(@ModelAttribute("empleadoWizard") Emp emp) {
        // El objeto 'emp' mantiene los datos del paso 1
        return "wizard/step2"; // Pide Departamento y Salario
    }

    @PostMapping("/finish")
    public String finishWizard(@ModelAttribute("empleadoWizard") Emp emp,
                               SessionStatus status) { // 2. Inyectamos SessionStatus
        // Aquí 'emp' tiene los datos del paso 1 Y del paso 2 acumulados
        empService.saveEmp(emp);
        status.setComplete(); // 3. IMPORTANTE: Limpia el objeto de la sesión
        return "redirect:/emp/list";
    }
}
```

#### Seguridad `CSRF` (Cross-Site Request Forgery)

Este es el concepto más crítico de seguridad web.

El Ataque:

* Tienes una sesión activa en tu app bancaria o de gestión.
* Recibes un correo spam con este HTML oculto: `<img src="http://tu-app.com/emp/delete/10" width="0" height="0">`.
* Tu navegador intenta cargar la imagen. Al hacerlo, envía una petición `GET` a esa `URL`.
* Como tienes cookies de sesión activas, el servidor acepta la petición y borra el registro 10.
* Ni siquiera viste la imagen, pero la acción se ejecutó.

* El método de borrar es un `@GetMapping`:

    ```java
    @GetMapping("/delete/{id}") // ¡PELIGRO!
    ```

    Riesgo de Seguridad: Si un atacante te envía un link por correo `img src="http://tu-app/emp/delete/5"`, y estás logueado, el navegador intentará cargar esa imagen y borrará el empleado sin que te des cuenta.

La Solucion:

* Semántica: Usar `POST`, `PUT` o `DELETE`. Los navegadores no ejecutan estos verbos automáticamente en etiquetas como `<img>` o `<a>`. Solo mediante formularios o JavaScript explícito.
* `CSRF Token`: `Spring Security` (por defecto) espera un token único generado por el servidor en cada petición de cambio de estado.
* Si un atacante intenta simular un `POST`, fallará porque no tiene el token secreto generado para tu sesión específica.

```html
<form action="..." method="post">
    <input type="hidden" name="_csrf" value="a4f3-g5h6-..." />
    ...
</form>
```

---

## Parte 6: `FrontEnd` con `Thymeleaf`

### Estructura y Modularidad `Fragments`

* `th:fragment="nombre"`: Define un bloque de código reutilizable.
  * Se le pueden pasar parametos como `th:fragment="header(title)"`

* `th:replace="~{ruta :: fragmento}"`: Reemplaza la etiqueta actual por el fragmento invocado. Es lo que uso para inyectar el navbar en cada página.
  * Ej: `th:replace="~{fragments/header :: header}"`
  * Tambien puedes pasar argumentos: `th:replace="~{fragments/header :: header(title='Inicio')}"`

### Navegacion y URLs

* `th:href="@{/ruta}"`: Genera URLs relativas al contexto de la aplicación.
  * Si la app se despliega en `miservidor.com/miapp/`, `Thymeleaf` agrega `/miapp` automáticamente. Sin esto, tus enlaces se romperían en producción.
  * Permite parámetros de consulta: `@{/emp/list(page=1, size=10)}` genera `/emp/list?page=1&size=10`.

* `th:action="@{/ruta}"`: Igual que `href`, pero para el atributo action de los formularios `HTML` (`<form>`).

### Renderizado de Texto y Datos

* `th:text="${variable}"`: Reemplaza el contenido de la etiqueta con el valor de la variable. Escapa caracteres especiales (`XSS protection`) por defecto.
  * Técnicas usadas en el código:
    * Ternario: `${condicion ? 'Si' : 'No'}`.
    * Safe Navigation (`?.`): `th:text="${emp.deptno?.dname}"`. Si deptno es null, no lanza error, simplemente no imprime nada.
    * Concatenación: `th:text="${m.ename + ' (' + m.empno + ')'}"`.

* `th:object="${objeto}"`: Vincula un formulario a un objeto `Java` (`Command Object`).
  * Efecto: Establece el contexto para usar `*{...}`.

* `th:field="*{propiedad}"`: Vincula un input, select o textarea a una propiedad del objeto definido en `th:object`.
  * Magia: Asigna automáticamente el `id`, `name`, y `value`. Si hay un error de validación, conserva el valor incorrecto para que el usuario no tenga que reescribirlo.

### Control de Flujo y Lógica

* `th:if="${condicion}"`: Renderiza la etiqueta solo si la condición es verdadera.
  * Ej: `th:if="${msg != null}"` para mostrar alertas de éxito.

* `th:each="item : ${lista}"`: Itera sobre una lista (bucle `for-each`). Repite la etiqueta `HTML` por cada elemento.
  * Ej: `th:each="emp : ${empPage.content}"` para llenar la tabla.

* `th:block`: Es un contenedor "fantasma". `Thymeleaf` procesa lo que hay dentro, pero la etiqueta `<th:block>` desaparece en el HTML final.
  * Ej: Lo use para definir variables locales (`th:with`) antes de incluir el `header`

### Manipulación de Atributos HTML

* `th:classappend="${condicion} ? 'clase-extra'"`: Añade una clase CSS si se cumple la condición, sin borrar las clases existentes.
  * Ej: Poner disabled en los botones de paginación.

* `th:value="${valor}"`: Establece el atributo value de un input estándar (cuando no usas `th:field`).

* `th:selected="${condicion}"`: Marca una opción (`<option>`) como seleccionada en un desplegable si la condición es `true`.

* `th:readonly="${condicion}"`: Hace que un input sea de solo lectura dinámicamente.
  * Ej: Bloquear el campo `empno` en modo edición.

### Validaciones y Utilidades

* `#fields.hasErrors('*')`: Objeto utilitario para verificar si hay errores de validación (`BindingResult`).

* `th:errors="*{campo}"`: Muestra el mensaje de error específico asociado a un campo.

* `th:errorclass="is-invalid"`: Aplica una clase CSS (ej. borde rojo de Bootstrap) al input solo si ese campo tiene errores.

* `#temporals.format(fecha, 'patron')`: Formatea objetos de la `API` `java.time` (como `LocalDate`) directamente en la vista.

### Anotaciones y Opciones Avanzadas en Thymeleaf

#### Thymeleaf Layout Dialect (Patrón Decorator)

Esta es la solución para evitar repetir código. A diferencia de `th:replace` (que solo incrusta fragmentos), el **Layout Dialect** invierte el control: las páginas hijas "decoran" a la página padre.

* **Requisito Previo:** Necesitas agregar la dependencia en tu `pom.xml` (si no usas `spring-boot-starter-thymeleaf` completo o versiones antiguas):

    ```xml
    <dependency>
        <groupId>nz.net.ultraq.thymeleaf</groupId>
        <artifactId>thymeleaf-layout-dialect</artifactId>
    </dependency>
    ```

* **Cómo funciona:**
  * **Base (`base.html`):** Define la estructura esqueleto (`<html>`, `<head>`, `<body>`, scripts comunes). Define "huecos" donde las hijas inyectarán contenido.
  * **Hija (`home.html`):** Solo contiene la data específica de esa vista.

    ```html
    <!DOCTYPE html>
    <html xmlns:th="[http://www.thymeleaf.org](http://www.thymeleaf.org)"
          xmlns:layout="[http://www.ultraq.net.nz/thymeleaf/layout](http://www.ultraq.net.nz/thymeleaf/layout)">
    <head>
        <title layout:title-pattern="$CONTENT_TITLE - AppBD">AppBD</title>
        </head>
    <body>
        <nav th:replace="~{fragments/navbar :: navbar}"></nav>

        <div layout:fragment="content"></div>

        <footer th:replace="~{fragments/footer :: footer}"></footer>

        <script src="...bootstrap.js..."></script>
        <th:block layout:fragment="page-scripts"></th:block>
    </body>
    </html>
    ```

    ```html
    <!DOCTYPE html>
    <html layout:decorate="~{base}">
    <head>
        <title>Inicio</title> </head>
    <body>
        <div layout:fragment="content">
            <h1>Bienvenido al Panel</h1>
        </div>

        <th:block layout:fragment="page-scripts">
             <script> console.log('Solo en home'); </script>
        </th:block>
    </body>
    </html>
    ```

#### Internacionalización (i18n)

Permite que tu aplicación hable varios idiomas sin duplicar HTML. Spring busca los archivos `.properties` basándose en el header `Accept-Language` del navegador o un parámetro de locale (`?lang=es`).

* **Estructura de Archivos:**
  * `src/main/resources/messages.properties` (Inglés/Default)
  * `src/main/resources/messages_es.properties` (Español)

* **Uso Avanzado (Parámetros):**
  Puedes pasar variables dinámicas a los mensajes de texto.

  *En `messages.properties`:*
  `welcome.user=Bienvenido, {0}! Hoy es {1}.`

  *En el HTML:*

    ```html
    <p th:text="#{welcome.user(${session.user.name}, ${diaActual})}"></p>
    ```

#### Inlining (JavaScript y Texto)

Es la capacidad de Thymeleaf de escribir valores directamente en el cuerpo del texto o dentro de bloques de script, escapando los caracteres automáticamente para evitar errores de sintaxis o seguridad.

* **`[[...]]` vs `[(...)]`:** Ejemplo insertando `<b>Hola</b>`
  * `[[${variable}]]`: Escapa el HTML (seguro, texto plano). Muestra `<b>Hola</b>`.
  * `[(${variable})]`: Renderiza el HTML (peligroso si viene del usuario). Muestra **Hola**.

* **JavaScript Inlining Inteligente:**
    Thymeleaf es inteligente con los tipos de datos en JS. Si la variable es un String, le pone comillas. Si es número, no. Si es un objeto, intenta serializarlo a JSON.

    ```html
    <script th:inline="javascript">
      // Thymeleaf añade comillas automáticamente si es String
      const username = [[${user.name}]];

      // Si es null, escribe 'null' (sin comillas) para no romper el JS
      const userAge = [[${user.age}]];

      // Serialización de objetos (útil para pasar datos a React/Vue/Charts)
      const employeesJson = [[${listaEmpleados}]];
    </script>
    ```

#### Variables de Estado (`iterStat`)

Cuando usas `th:each`, Thymeleaf crea automáticamente un objeto de estado. Si no le pones nombre, se llama `nombreVariableStat`.

* **Propiedades útiles de `iterStat`:**
  * `.index`: Índice base 0 (0, 1, 2...).
  * `.count`: Contador base 1 (1, 2, 3...).
  * `.size`: Tamaño total de la lista.
  * `.even` / `.odd`: Booleano, true si es par/impar.
  * `.first` / `.last`: Booleano, útil para estilos CSS (ej. bordes redondeados solo en el primero y último).

    ```html
    <tr th:each="emp, stat : ${emps}"
        th:classappend="${stat.even}? 'table-light' : ''">
        <td th:text="${stat.count}"></td> <td th:text="${emp.ename}">
            <span th:if="${stat.last}" class="badge bg-new">Nuevo</span>
        </td>
    </tr>
    ```

#### Switch / Case

Funciona igual que en Java. Es preferible al `th:if` cuando tienes más de dos condiciones exclusivas, ya que Thymeleaf detiene la evaluación apenas encuentra una coincidencia (mejor rendimiento).

* **El comodín `*`:** Siempre debe ir al final y actúa como el `default`.

    ```html
    <div th:switch="${emp.job}">
      <span th:case="'PRESIDENT'" class="badge bg-danger">Jefe Supremo</span>
      <span th:case="'MANAGER'"   class="badge bg-warning">Gerente</span>
      <span th:case="'ANALYST'"   class="badge bg-info">Analista</span>
      <span th:case="*" class="badge bg-secondary">Staff</span>
    </div>
    ```

#### `th:attr` y Atributos Dinámicos

A veces necesitas tocar atributos HTML que no son estándares o que Thymeleaf no mapea directamente (como `data-*` attributes para JavaScript).

* **`th:attr`:** Permite asignar cualquier atributo.

    ```html
    <tr th:attr="data-id=${emp.id}, data-role=${emp.role}">...</tr>
    ```

* **`th:style` vs `th:styleappend`:**
  * `th:style`: Reemplaza todo el estilo inline.
  * `th:styleappend`: Agrega estilos sin borrar los que ya existan.

    ```html
    <div class="progress-bar"
         th:style="'width: ' + ${porcentajeAvance} + '%'"
         th:classappend="${porcentajeAvance > 90} ? 'bg-success' : 'bg-primary'">
    </div>
    ```
