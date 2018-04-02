/*
 * Copyright (C) 2018 diegues
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hijack;

/**
 *
 * @author diegues
 */
public class EunisClassification {
    
    private String filename;
    private String classValue;

    public EunisClassification(String filename, String classValue){
        this.filename = filename;
        this.classValue = classValue;
    }

    public boolean isValid(){
        char level0 = getClassValue().charAt(0);
        if(!Character.isAlphabetic(level0)){
            return false;
        }
        int length = getClassValue().length();
        for(int i = 1; i < length; i++){
            if(!Character.isDigit(getClassValue().charAt(i)) && getClassValue().charAt(i) != '.'){
                return false;
            }
        }
        return true;
    }

    public String getClassValue() {
        return classValue;
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
       this.filename = filename;
    }

    @Override
    public String toString() {
        return getFilename() + "," + getClassValue(); //To change body of generated methods, choose Tools | Templates.
    }
}
