package sportclub.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import sportclub.domain.model.*;

public class MemberManagementService {
    private final List<Member> members = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Player addPlayer(
            String name,
            int age,
            String team,
            String position,
            int jerseyNumber,
            BigDecimal salary) {
        Player player = new Player(name, age, team, position, jerseyNumber, salary);
        setMemberId(player);
        members.add(player);
        return player;
    }

    public Coach addCoach(
            String name,
            int age,
            String team,
            String specialization,
            String certification,
            BigDecimal salary) {
        Coach coach = new Coach(name, age, team, specialization, certification, salary);
        setMemberId(coach);
        members.add(coach);
        return coach;
    }

    public Manager addManager(
            String name,
            int age,
            String team,
            String department,
            String responsibilities,
            BigDecimal salary) {
        Manager manager = new Manager(name, age, team, department, responsibilities, salary);
        setMemberId(manager);
        members.add(manager);
        return manager;
    }

    private void setMemberId(Member member) {
        member.setId(idCounter.getAndIncrement());
    }

    public boolean removeMember(int id) {
        return members.removeIf(member -> member.getId() == id);
    }

    public Optional<Member> findMemberById(int id) {
        return members.stream().filter(member -> member.getId() == id).findFirst();
    }

    public boolean updateMember(Member updatedMember) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == updatedMember.getId()) {
                members.set(i, updatedMember);
                return true;
            }
        }
        return false;
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    public void clearAll() {
        members.clear();
        resetIdCounter();
    }

    public void resetIdCounter() {
        idCounter.set(1);
        Member.resetIdCounter();
    }

    public int getMemberCount() {
        return members.size();
    }
}
