package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static  final  String username="root";
    private static  final  String password="";

    public static  void main (String[] args){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection= DriverManager.getConnection( url , username , password) ;
            Patient patient = new Patient(connection , scanner);
            Doctor doctor = new Doctor(connection ) ;

            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add patient");
                System.out.println("2. View patiens");
                System.out.println("3. View doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("6. Enter your choice ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1 :
                        // add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2 :
                        //view patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3 :
                        //view doctor
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4 :
                        // Appointment
                        bookAppointment( patient , doctor ,connection , scanner );
                        System.out.println();
                        break;
                    case 5 :
                        return;
                    default:
                        System.out.println( "Enter valid choice !!");
                }




            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public  static  void  bookAppointment(Patient patient , Doctor doctor , Connection connection , Scanner scanner){
        System.out.print("Enter Patient id ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor id ");
        int doctorId = scanner.nextInt();
        System.out.print("enter appointment date (YYYY-MM-DD)");
        String appointmentdate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if (checkDoctorAvailability(doctorId , appointmentdate , connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date ) VALUES ( ? , ? , ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1 , patientId);
                    preparedStatement.setInt(2 , doctorId);
                    preparedStatement.setString(3 , appointmentdate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected>0){
                        System.out.println("appointment Booked !");
                    }else{
                        System.out.println("Failed to book  appointment");
                    }
                }catch (SQLException e ){
                    e.printStackTrace();

                }

            }

        }else{
            System.out.println("either doctor or patient doesn't exist");
        }
    }


    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}