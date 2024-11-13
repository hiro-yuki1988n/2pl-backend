package al_hiro.com.Mkoba.Management.System.entity;

import al_hiro.com.Mkoba.Management.System.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    public void update() {
        Member member = new Member();
        member.setName(this.name);
        member.setEmail(this.email);
        member.setPhone(this.phone);
        member.setPassword(this.password);
        member.setMemberRole(this.memberRole);
    }
}
