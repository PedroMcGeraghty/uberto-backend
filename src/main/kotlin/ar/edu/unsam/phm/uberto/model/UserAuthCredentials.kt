package ar.edu.unsam.phm.uberto.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

enum class Role {
    DRIVER,
    PASSENGER
}

@Entity
class UserAuthCredentials() : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 255)
    private lateinit var username: String

    @Column(length = 550)
    private lateinit var password: String

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    lateinit var role: Role

    @Column(nullable = false)
    private var accountNonExpired: Boolean = true

    @Column(nullable = false)
    private var accountNonLocked: Boolean = true

    @Column(nullable = false)
    private var credentialsNonExpired: Boolean = true

    @Column(nullable = false)
    private var enabled: Boolean = true


    override fun getPassword(): String = this.password
    fun setPassword(encodedPassword: String) {
        this.password = encodedPassword
    }

    override fun getUsername(): String = this.username
    fun setUsername(username: String) {
        this.username = username
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${this.role.name}"))
    }

    override fun isAccountNonExpired(): Boolean = this.accountNonExpired
    override fun isAccountNonLocked(): Boolean = this.accountNonLocked
    override fun isCredentialsNonExpired(): Boolean = this.credentialsNonExpired
    override fun isEnabled(): Boolean = this.enabled
    fun lockAccount() {
        accountNonLocked = false
    }

    fun unlockAccount() {
        accountNonLocked = true
    }

    fun expireAccount() {
        accountNonExpired = false
    }

    fun expireCredentials() {
        credentialsNonExpired = false
    }

    fun disable() {
        enabled = false
    }

    fun enable() {
        enabled = true
    }

}