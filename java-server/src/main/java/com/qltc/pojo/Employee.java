/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qltc.pojo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sonho
 */
@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull(message = "{employee.firstName.notNull}")
    @Size(min = 1, max = 10, message = "{employee.firstName.lenErr}")
    private String firstName;
    @Basic(optional = false)
    @NotNull(message = "{employee.lastName.notNull}")
    @Size(min = 1, max = 10, message = "{employee.lastName.lenErr}")
    private String lastName;
    @Basic(optional = false)
    @Column(unique = true)
    private String identityNumber;
    @Basic(optional = false)
    private String position;
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "branchId", referencedColumnName = "id")
    private Branch branchId;
}