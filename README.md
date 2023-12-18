# Facebook Application
This Application Uses MySQL Database...
##### :purple_square: Its an API Based Web Application
## :one: Frameworks and Languages Used -
    1. SpringBoot
    2. JAVA
    3. Postman
    4. SQL
    
## :two: Dependency Used
    1. Spring Web
    2. Spring Boot Dev Tools
    3. Lombok
    4. Spring Data JPA
    5. MySQL Driver
    6. Jakarta
    7. Javax
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
## :three: Dataflow (Functions Used In)
### :purple_square: 1. Model - Model is used to Initialize the required attributes and create the accessable constructors and methods
#### #️⃣: User.java
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    @NotEmpty
    private String firstName;

    @Column(nullable = false)
    @NotEmpty
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotEmpty
    private String facebookName;

    private String profilePicture;

    private String facebookBio;

    @Column(nullable = false)
    @NotEmpty
    private String password;

    @Column(nullable = false)
    @Past // check by testing/passing future date
    @NotNull
    private LocalDate dOB;

    @Column(unique = true , nullable = false)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{2}-\\d{10}", message = "Phone number should be in the format XX-XXXXXXXXXX")
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isBlueTicked;// this should not be exposed to user : Hint : DTO
}

```
#### #️⃣: Post.java
```java
@Data
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @NotEmpty
    private String postData;

    private String postCaption;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)// remove this ...not needed...why ??
    @JoinColumn(nullable = false , name = "fk_user_ID")
    @JsonIgnore
    private User user;
}
```
#### #️⃣: AuthenticationToken.java
```java
@Data
@NoArgsConstructor
@Entity
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String token;
    private LocalDate tokenCreationDate;

    @OneToOne
    @JoinColumn(nullable = false , name = "fk_user_ID")
    private User user;

    public AuthenticationToken(User user) {
        this.user = user;
        this.tokenCreationDate = LocalDate.now();
        this.token = UUID.randomUUID().toString();
    }
}
```
##### To See Model
:white_check_mark: [Facebook-Model](https://github.com/harshsikarwar20/Facebook/tree/master/src/main/java/com/example/Facebook/models)
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

### :purple_square: 2. Service - This Layer is used to write the logic of our CRUD operaions.
#### #️⃣: UserService.java
```java
@Service
public class UserService {

    @Autowired
    public IUserRepository userRepository;

    @Autowired
    public TokenService tokenService;

    @Autowired
    public ITokenRepository tokenRepository;

    @Autowired
    FollowingService followingService;

    @Autowired
    FollowerService followerService;

    @Autowired
    LikeService likeService;
    public SignUpOutput signUp(User signUpDto) {
        // Check whether this email exits in the database or not

        User user = userRepository.findFirstByEmail(signUpDto.getEmail());

        if(user!=null){
            throw new IllegalStateException("Facebook user already exists!! please signIn instead...");
        }
        // Now encrypt the password...
        String encryptedPassword = null;

        try{
            encryptedPassword = encryptPassword(signUpDto.getPassword());
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        signUpDto.setPassword(encryptedPassword);
        userRepository.save(signUpDto);

        return new SignUpOutput("Facebook user registered","Facebook account created successfully");
    }

    private String encryptPassword(String userPassword)throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(userPassword.getBytes());
        byte[] digested = md5.digest();

        String hash = DatatypeConverter.printHexBinary(digested);

        return hash;
    }
    public SignInOutput signIn(SignInInput signInDto) {
        //check if user exists or not based on email
        User user = userRepository.findFirstByEmail(signInDto.getEmail());
        if(user == null) {
            throw new IllegalStateException("User invalid!!!!...sign up instead");
        }

        String encryptedPassword = null;

        try {
            encryptedPassword = encryptPassword(signInDto.getPassword());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //match it with database encrypted password

        boolean isPasswordValid = encryptedPassword.equals(user.getPassword());

        if(!isPasswordValid) {
            throw new IllegalStateException("User invalid!!!!...sign up instead");
        }

        AuthenticationToken token = new AuthenticationToken(user);

        tokenService.saveToken(token);

        //set up output response

        return new SignInOutput("Authentication Successfull!!!", token.getToken());
    }

    public void updateUser(User user, String token) {
        User originalUser = tokenRepository.findFirstByToken(token).getUser();

        if(!(user.getFirstName().isEmpty())){
            originalUser.setFirstName(user.getFirstName());
        }

//        if(user.getLastName()!=null){
        if(!(user.getLastName().isEmpty())){
            originalUser.setLastName(user.getLastName());
        }

        if(user.getPassword()!=null){
            String encryptedPassword = null;

            try{
                encryptedPassword = encryptPassword(user.getPassword());
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            originalUser.setPassword(encryptedPassword);
        }

        if((user.getPhoneNumber()!=null)){
            Pattern p = Pattern.compile("\\d{2}-\\d{10}");

            Matcher m = p.matcher(user.getPhoneNumber());
            if( (m.find() && m.group().equals(user.getPhoneNumber()))){
                originalUser.setPhoneNumber(user.getPhoneNumber());
            }else{
                throw new IllegalStateException("Enter correct details");
            }
        }

        if(user.getEmail()!=null){
            Pattern p = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\\\.[a-z]{2,3}");
            Matcher m = p.matcher(user.getEmail());

            if(m.find() && m.group().equals(user.getEmail())){
                originalUser.setEmail(user.getEmail());
            }else{
                throw new IllegalStateException("Enter correct details");
            }
        }
        userRepository.save(originalUser);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }


    public String deleteById(Long userId) {
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }

    public String toggleBlueTick(Long id, boolean blueTick) {
        User user = userRepository.findByUserId(id);

        if(user!=null){
            user.setBlueTicked(blueTick);
            userRepository.save(user);
            return "Blue Tick was set to..." + blueTick;
        }else{
            return "Invalid User...";
        }
    }
    @Transactional
    public String followUser(Long userId, Long otherId) {

        if(userId == otherId) {
            return "Cant follow yourself!!!!";
        }
        User myUser = userRepository.findByUserId(userId);
        User otherUser = userRepository.findByUserId(otherId);

        if(myUser!=null && otherUser!=null) {
            //todo : check if already follows or not
            //follow from my side
            followingService.saveFollowing(myUser,otherUser);
            //follower from other side
            followerService.saveFollower(otherUser, myUser);
            return "Followed Successfully!!!!!";
        }
        else {
            return "Users are invalid!!!";
        }
    }

    public void like(PostLike postLike) {
        likeService.like(postLike);
    }
}

```

#### #️⃣: PostService.java
```java
@Service
public class PostService {

    @Autowired
    IPostRepository postRepository;

    @Autowired
    ITokenRepository tokenRepository;

    @Autowired
    LikeService likeService;

    public void addPost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPost() {
        return postRepository.findAll();
    }

    public List<Post> getAllPosts(String token) {
        User user = tokenRepository.findFirstByToken(token).getUser();
        List<Post> postList = postRepository.findByUser(user);
        return postList;
    }

    public long getLikes(Long postId) {
        return likeService.getLikes(postId);
    }
}

```

#### #️⃣: TokenService.java
```java
@Service
public class TokenService {

    @Autowired
    public ITokenRepository tokenRepository;

    public void saveToken(AuthenticationToken token) {
        tokenRepository.save(token);
    }

    public boolean authenticate(String email, String token) {
        if(token==null && email==null){
            return false;
        }

        AuthenticationToken authToken = tokenRepository.findFirstByToken(token);

        if(authToken==null){
            return false;
        }

        String expectedEmail = authToken.getUser().getEmail();
        return expectedEmail.equals(email);
    }

    public void deleteToken(String token) {
        AuthenticationToken token1 = tokenRepository.findFirstByToken(token);
        tokenRepository.deleteById(token1.getTokenId());
    }

    public User findUserByToken(String token) {
        return tokenRepository.findFirstByToken(token).getUser();
    }
}
```

#### To See Service
:white_check_mark: [Facebook-Service](https://github.com/harshsikarwar20/Facebook/tree/master/src/main/java/com/example/Facebook/services)
----------------------------------------------------------------------------------------------------------------------------------------------------

### :purple_square: 3. Controller - This Controller is used like UI between Model and Service and also for CRUD Mappings.
#### #️⃣: UserController.java
```java
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    public TokenService tokenService;


    @PostMapping(value = "/signUp")
    public SignUpOutput signUp(@Valid @RequestBody User signUpDto){
        return userService.signUp(signUpDto);
    }


    @PostMapping(value = "/signIn")
    public SignInOutput signIn(@Valid @RequestBody SignInInput signInDto){
        return userService.signIn(signInDto);
    }


    @DeleteMapping(value = "/signOut")
    public ResponseEntity<String> signOut(@RequestParam String email , @RequestParam String token){
        HttpStatus status = null;
        String message = null;

        if(tokenService.authenticate(email,token)){
            tokenService.deleteToken(token);
            message = "Sign Out Successful";
            status = HttpStatus.OK;
        }else{
            message = "Invalid User";
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<String>(message,status);
    }

    @PutMapping()
    public ResponseEntity<String> updateUser(@RequestBody String email , @RequestParam String token , @RequestBody User user){
        HttpStatus status;
        String message = null;

        if(tokenService.authenticate(email,token)){
            try{
                userService.updateUser(user,token);
                status = HttpStatus.OK;
                message = "User Updated successfully";
            }catch(Exception e){
                status = HttpStatus.BAD_REQUEST;
                message = "Enter valid Information";
            }
        }else{
            status = HttpStatus.FORBIDDEN;
            message = "Invalid User";
        }
        return new ResponseEntity<String>(message,status);
    }

    @PostMapping(value = "/follow/{userId}/{otherId}")
    private String followUser(@PathVariable Long userId , @PathVariable Long otherId){
        return userService.followUser(userId,otherId);
    }

    @PostMapping("/like")
    void likePost(@RequestBody PostLike postLike){
        //todo : validation
        userService.like(postLike);
    }
}
```

#### #️⃣: PostController.java
```java
@RestController
@RequestMapping(value = "/Post")
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    TokenService tokenService;
    @PostMapping()
    private ResponseEntity<String> addPost(@Valid @RequestParam String email , @RequestParam String token , @RequestBody Post post){
        HttpStatus status;
        String message = null;

        if(tokenService.authenticate(email,token)){
            User user = tokenService.findUserByToken(token);
            post.setUser(user); // Setting user in the post...
            postService.addPost(post);
            message = "Post added successfully";
            status = HttpStatus.OK;
        }else{
            message = "Invalid user";
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<String>(message,status);
    }

    @GetMapping()
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam String email , @RequestParam String token){
        HttpStatus status;
        List<Post> postList = null;
        if(tokenService.authenticate(email,token)) {
            postList = postService.getAllPosts(token);
            status = HttpStatus.OK;
        }
        else {
            status = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<List<Post>>(postList , status);
    }

    @GetMapping("/{postId}/likeCount")
    long getLikesForPost(@PathVariable Long postId) {
        return postService.getLikes(postId);
    }

}
```

#### To See Controller
:white_check_mark: [Facebook-Controller](https://github.com/harshsikarwar20/Facebook/tree/master/src/main/java/com/example/Facebook/controllers)
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
### :purple_square: 3. Repository : data access object (DAO) is an object that provides an abstract interface to some type of database or other persistence mechanisms.
#### #️⃣: IUserRepository.java
```java
@Repository
public interface IUserRepository extends JpaRepository<User,Long> {
    
    User findFirstByEmail(String email);

    User findByUserId(Long id);

}

```

#### #️⃣: IPostRepository.java
```java
@Repository
public interface IPostRepository extends JpaRepository<Post,Integer> {

    List<Post> findByUser(User user);

}
```

#### #️⃣: ITokenRepo.java
```java
@Repository
public interface ITokenRepository extends JpaRepository<AuthenticationToken , Long> {
    AuthenticationToken findFirstByToken(String token);

}
```

#### To See Repository
:white_check_mark: [Facebook-DAO](https://github.com/harshsikarwar20/Facebook/tree/master/src/main/java/com/example/Facebook/repository)
-------------------------------------------------------------------------------------------------------------------------------------------------------
### :purple_square: 4. DTO :  This Layer is for SignIn and SignUp Authentication

#### #️⃣: SignUpOutput.java

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpOutput {
    private String status;
    private String message;
}
```

#### #️⃣: SignInInput.java

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInInput {
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotEmpty
    private String password;
}
```

#### #️⃣: SignInOutput.java
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInOutput {
    private String status;
    private String token;
}
```

#### To See DTO
:white_check_mark: [Facebook-DTO](https://github.com/harshsikarwar20/Facebook/tree/master/src/main/java/com/example/Facebook/dto)    
-------------------------------------------------------------------------------------------------------------------------------------------------------
## :four: DataStructures Used in Project
    1. ResponseEntity
    2. List
    3. Json
-------------------------------------------------------------------------------------------------------------------------------------------------------
