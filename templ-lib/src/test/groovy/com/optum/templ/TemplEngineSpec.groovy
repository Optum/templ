package com.optum.templ

import com.optum.templ.exceptions.MissingKeyTemplException
import spock.lang.Specification

class TemplEngineSpec extends Specification {

    Map<String, String> map = new HashMap<>()
    MapDelegateTemplDataSource ds = new MapDelegateTemplDataSource(map)

    def "Template Substitution Simple" () {
        when:
        map.clear()
        map.put("D", "1")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{D}}Z' | 'A1Z'
    }

    def "Template Substitution Prefix" () {
        when:
        map.clear()
        map.put("D", "1")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{ D}}Z' | 'A 1Z'
        'A{{,D}}Z' | 'A,1Z'
        'A{{.D}}Z' | 'A.1Z'
        'A{{;D}}Z' | 'A;1Z'
        'A{{:D}}Z' | 'A:1Z'
        'A{{?D}}Z' | 'A?1Z'
        'A{{&D}}Z' | 'A&1Z'
        'A{{@D}}Z' | 'A@1Z'
        'A{{#D}}Z' | 'A#1Z'
        'A{{/D}}Z' | 'A/1Z'
        'A{{(D}}Z' | 'A(1Z'
        'A{{)D}}Z' | 'A)1Z'
        'A{{<D}}Z' | 'A<1Z'
        'A{{>D}}Z' | 'A>1Z'
        'A{{_D}}Z' | 'A_1Z'
        'A{{-D}}Z' | 'A-1Z'
        'A{{\\D}}Z' | 'A\\1Z'
        'A{{|D}}Z' | 'A|1Z'
    }

    def "Template Substitution Suffix" () {
        when:
        map.clear()
        map.put("D", "1")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{D }}Z' | 'A1 Z'
        'A{{D,}}Z' | 'A1,Z'
        'A{{D.}}Z' | 'A1.Z'
        'A{{D;}}Z' | 'A1;Z'
        'A{{D:}}Z' | 'A1:Z'
        'A{{D?}}Z' | 'A1?Z'
        'A{{D&}}Z' | 'A1&Z'
        'A{{D@}}Z' | 'A1@Z'
        'A{{D#}}Z' | 'A1#Z'
        'A{{D/}}Z' | 'A1/Z'
        'A{{D(}}Z' | 'A1(Z'
        'A{{D)}}Z' | 'A1)Z'
        'A{{D<}}Z' | 'A1<Z'
        'A{{D>}}Z' | 'A1>Z'
        'A{{D_}}Z' | 'A1_Z'
        'A{{D-}}Z' | 'A1-Z'
        'A{{D\\}}Z' | 'A1\\Z'
        'A{{D|}}Z' | 'A1|Z'
    }

    def "Template Substitution Combinations" () {
        when:
        map.clear()
        map.put("D", "1")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{.D.}}Z' | 'A.1.Z'
        'A{{(D)}}Z' | 'A(1)Z'
        'A{{ (D) }}Z' | 'A (1) Z'
        'A{{?D_}}Z' | 'A?1_Z'
        'A{{--(D):}}Z' | 'A--(1):Z'
    }

    def "Template Substitution Empty Combinations" () {
        when:
        map.clear()
        map.put("D", "")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{.D.}}Z' | 'AZ'
        'A{{(D)}}Z' | 'AZ'
        'A{{?D_}}Z' | 'AZ'
        'A{{--(D):}}Z' | 'AZ'
    }


    def "Template Substitution Nested" () {
        when:
        map.clear()
        map.put("D", "1")
        map.put("E", "2")
        map.put("F", "3")
        map.put("D2", "6")
        map.put("D3", "")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{D}}Z' | 'A1Z'
        'A{{E}}Z' | 'A2Z'
        'A{{D{{E}}}}Z' | 'A6Z'
        'A{{D{{F}}}}Z' | 'AZ'
        'A{{,-D{{E}}.,}}Z' | 'A,-6.,Z'
        'A{{, (D{{E}}). ,}}Z' | 'A, (6). ,Z'
        'A{{,:D{{F}};,}}Z' | 'AZ'
    }

    def "Template Substitution Nested Example Use Cases" () {
        when:
        map.clear()
        map.put("HOST", "dev.example.com")
        map.put("HOST_A", "one.example.com")
        map.put("HOST_B", "two.example.com")
        map.put("HOSTIDA", "A")
        map.put("HOSTIDB", "B")
        map.put("HOSTIDBLANK", "")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'src://{{HOST_{{HOSTIDA}}}}/index.html' | 'src://one.example.com/index.html'
        'src://{{HOST{{_HOSTIDA}}}}/index.html' | 'src://one.example.com/index.html'
        'src://{{HOST_{{HOSTIDB}}}}/index.html' | 'src://two.example.com/index.html'
        'src://{{HOST{{_HOSTIDB}}}}/index.html' | 'src://two.example.com/index.html'
        'src://{{HOST_{{HOSTIDBLANK}}}}/index.html' | 'src://dev.example.com_/index.html'
        'src://{{HOST{{_HOSTIDBLANK}}}}/index.html' | 'src://dev.example.com/index.html'
    }

    def "Template Substitution Allowed Missing Value" () {
        when:
        map.clear()
        TemplEngine te = new TemplEngine(ds, true)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template  | expected
        'A{{.D.}}Z' | 'AZ'
    }

    def "Template Substitution Supports Uppercase Results" () {
        when:
        map.clear()
        map.put("ENV", "dev-a_b")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template        | expected
        'A{{^ENV}}Z'    | 'ADEV_A_BZ'
        'A{{.^ENV}}Z'   | 'A.DEV_A_BZ'
        'A{{.__^ENV}}Z' | 'A.__DEV_A_BZ'
        'A{{^ENV.}}Z'   | 'ADEV_A_B.Z'
        'A{{.^ENV.}}Z'  | 'A.DEV_A_B.Z'
    }

    def "Template Substitution Supports Lowercase Results" () {
        when:
        map.clear()
        map.put("ENV", "DEV-A_B")
        TemplEngine te = new TemplEngine(ds)
        String result = te.processTemplate(template)

        then:
        result == expected

        where:
        template        | expected
        'A{{~ENV}}Z'    | 'Adev-a-bZ'
        'A{{.~ENV}}Z'   | 'A.dev-a-bZ'
        'A{{.__~ENV}}Z' | 'A.__dev-a-bZ'
        'A{{~ENV.}}Z'   | 'Adev-a-b.Z'
        'A{{.~ENV.}}Z'  | 'A.dev-a-b.Z'
    }

    def "Exception - Template Substitution Missing Value" () {
        when:
        map.clear()
        TemplEngine te = new TemplEngine(ds)
        te.processTemplate('A{{D}}Z')

        then:
        MissingKeyTemplException ex = thrown()
        ex.key == 'D'
    }

}